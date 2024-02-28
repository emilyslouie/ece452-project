package com.example.palletify.ui.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.R
import com.example.palletify.ui.theme.PalletifyTheme
import kotlin.concurrent.thread
import kotlin.math.pow

@Composable
fun GeneratorScreen(generatorViewModel: GeneratorViewModel = viewModel()) {
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val localDensity = LocalDensity.current
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    // TODO: ensure that when the "more options button" is placed, that the generate button is in the middle
                ) {
                    Row() {
                        IconButton(
                            onClick = { generatorViewModel.handleUndo() },
                            enabled = generatorUiState.palettesInUndoStack > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo");
                        }
                        IconButton(
                            onClick = { generatorViewModel.handleRedo() },
                            enabled = generatorUiState.palettesInRedoStack > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo");
                        }
                    }
                    Button(onClick = {
                        if (generatorUiState.lockedColors.size == 1) {
                            // TODO: Change undo/redo logic to update lockedColors for current palette
                            val seedColor = generatorUiState.lockedColors.iterator().next();
                            thread { generatorViewModel.getNewPaletteWithSeed(seedColor) }
                        } else if (generatorUiState.lockedColors.size > 1) {
                            // TODO: Send multiple requests for each seed, then combine results into one palette
                        } else {
                            // If no locked colours, generate palette with random seed
                            thread { generatorViewModel.getNewRandomPalette() }
                        }
                    }) {
                        Text(
                            modifier = Modifier.padding(end = 4.dp),
                            text = stringResource(R.string.generate),
                            style = typography.titleLarge
                        )
                        Icon(Icons.Filled.Refresh, contentDescription = "Regenerate")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .safeDrawingPadding()
                .padding(innerPadding)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
                    // Get the available height of the container
                    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Palette(
                generatorViewModel = generatorViewModel,
                colors = generatorUiState.currentPalette,
                numOfColors = generatorUiState.numberOfColours,
                heightAvailable = columnHeightDp
            )
        }
    }
}


@Composable
fun Palette(generatorViewModel: GeneratorViewModel, colors: List<com.example.palletify.data.Color>, numOfColors: Int, heightAvailable: Dp) {
    val heightPerColor = heightAvailable / numOfColors;
    // TODO: probably need to change this to be based on the number of colours so that we can add/subtract num of colours in a palette
    colors.forEach { color ->
        ColorInPalette(generatorViewModel, color, heightPerColor)
    }
}

// Formula to calculate the luminosity of the colour was referenced from:
// https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
fun calculateLuminosity(red: Float, green: Float, blue: Float): Double {
    return (0.2126 * calculateRgbFraction(red)) + (0.7152 * calculateRgbFraction(green)) + (0.0722 * calculateRgbFraction(
        blue
    ))
}

fun calculateRgbFraction(color: Float): Double {
    if (color <= 0.03928) {
        return color / 12.92
    }
    return ((color + 0.055) / 1.055).pow(2.4)
}

@Composable
fun ColorInPalette(generatorViewModel: GeneratorViewModel, color: com.example.palletify.data.Color, heightPerColor: Dp) {
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val backgroundColor = Color(color.rgb.fraction.r, color.rgb.fraction.g, color.rgb.fraction.b)
    val luminosity =
        calculateLuminosity(color.rgb.fraction.r, color.rgb.fraction.g, color.rgb.fraction.b)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightPerColor)
            .background(backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Column(
            Modifier
                .padding(top = 16.dp, bottom = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = color.hex.clean,
                color = if (luminosity >= 0.179) Color.Black else Color.White,
                style = typography.headlineSmall
            )
            Text(
                text = color.name.value,
                color = if (luminosity >= 0.179) Color(35, 35, 35) else Color(230, 230, 230),
                style = typography.labelMedium
            )
        }

        if (!generatorUiState.lockedColors.contains(color)) {
            // Button to lock a colour
            IconButton(
                onClick = { generatorViewModel.handleLockForColor(color) }
            ) {
                Icon(
                    Icons.Filled.LockOpen,
                    contentDescription = "Lock a colour",
                    tint = if (luminosity >= 0.179) Color.Black else Color.White
                )
            }
        } else {
            // Button to unlock a colour
            IconButton(
                onClick = { generatorViewModel.handleUnlockForColor(color) }
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Unlock a colour",
                    tint = if (luminosity >= 0.179) Color.Black else Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GeneratorScreenPreview() {
    PalletifyTheme {
        GeneratorScreen()
    }
}