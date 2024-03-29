package com.example.palletify.ui.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.ColorUtils.hexToComposeColor
import com.example.palletify.data.GenerationMode
import com.example.palletify.database.Palette
import com.example.palletify.database.PaletteViewModel
import com.example.palletify.ui.components.BottomSheet
import com.example.palletify.ui.components.TrademarkComponentWrapper
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
    val context = LocalContext.current
    val paletteViewModel =
        ViewModelProvider(context as ViewModelStoreOwner)[PaletteViewModel::class.java]

    var activeColor by rememberSaveable {
        mutableStateOf<com.example.palletify.data.Palette.Color?>(
            null
        )
    }

    // Bottom sheet that is shown when clicking on the name the color
    if (activeColor != null) {
        BottomSheet(onDismiss = { activeColor = null }) {
            Column(Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(modifier = Modifier.clickable(
                        enabled = generatorUiState.numberOfColours < 6,
                        onClick = {
                            generatorViewModel.handleIncreaseNumOfColors(
                                activeColor!!
                            )
                            activeColor = null
                        }
                    ),
                        color = if (generatorUiState.numberOfColours < 6) Color.Black else Color.LightGray,
                        text = "Add a new color to palette")
                }
                HorizontalDivider()
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(modifier = Modifier.clickable(
                        enabled = generatorUiState.numberOfColours > 3,
                        onClick = {
                            generatorViewModel.handleDecreaseNumOfColors(
                                activeColor!!
                            )
                            activeColor = null
                        }
                    ),
                        color = if (generatorUiState.numberOfColours > 3) Color.Black else Color.LightGray,
                        text = "Remove this color from palette")
                }
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
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
                            // Undo button
                            IconButton(
                                onClick = { generatorViewModel.handleUndo() },
                                enabled = generatorUiState.palettesInUndoStack > 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo");
                            }
                            // Redo button
                            IconButton(
                                onClick = { generatorViewModel.handleRedo() },
                                enabled = generatorUiState.palettesInRedoStack > 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo");
                            }
                        }
                        // Generate button
                        Button(onClick = {
                            thread { generatorViewModel.getNewPalette() }
                        }) {
                            Text(
                                modifier = Modifier.padding(end = 4.dp),
                                text = "Generate",
                                style = typography.titleLarge
                            )
                            Icon(Icons.Filled.Refresh, contentDescription = "Regenerate")
                        }
                        // Save Button
                        Button(onClick = {
                            val currentPalette = generatorViewModel.uiState.value.currentPalette
                            val numberOfColors = generatorViewModel.uiState.value.numberOfColours
                            val mode = generatorViewModel.uiState.value.mode
                            val colorsList = mutableListOf<String>()
                            for (i in 0 until numberOfColors) {
                                val color = currentPalette[i].hex.value
                                colorsList.add(color)
                            }
                            val palette = Palette(
                                0,
                                numberOfColors,
                                colorsList,
                                mode.mode,
                                favourite = false
                            )
                            paletteViewModel.addPalette(palette)
                        }
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 4.dp),
                                text = "Save",
                                style = typography.titleLarge
                            )
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
                    .fillMaxWidth()
            ) {
                // Dropdown menu to select a generation mode
                GenerationModeDropdown(generatorViewModel)

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned { coordinates ->
                            // Get the available height of the container
                            columnHeightDp =
                                with(localDensity) { coordinates.size.height.toDp() }
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Palette(
                        generatorViewModel = generatorViewModel,
                        colors = generatorUiState.currentPalette,
                        numOfColors = generatorUiState.numberOfColours,
                        heightAvailable = columnHeightDp
                    ) { selectedColor ->
                        activeColor = selectedColor
                    }
                }
            }
        }
    }
}


@Composable
fun Palette(
    generatorViewModel: GeneratorViewModel,
    colors: MutableList<com.example.palletify.data.Palette.Color>,
    numOfColors: Int,
    heightAvailable: Dp,
    onItemClick: (com.example.palletify.data.Palette.Color) -> Unit
) {
    val heightPerColor = heightAvailable / numOfColors;
    colors.forEach { color ->
        ColorInPalette(
            generatorViewModel,
            color,
            heightPerColor,
            onItemClick
        )
    }
}

// Formula to calculate the luminosity of the colour was referenced from:
// https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
fun calculateLuminosity(red: Int, green: Int, blue: Int): Double {
    val r = red / 255f;
    val g = green / 255f;
    val b = blue / 255f;
    return (0.2126 * calculateRgbFraction(r)) + (0.7152 * calculateRgbFraction(g)) + (0.0722 * calculateRgbFraction(
        b
    ))
}

fun calculateRgbFraction(color: Float): Double {
    if (color <= 0.03928) {
        return color / 12.92
    }
    return ((color + 0.055) / 1.055).pow(2.4)
}

@Composable
fun ColorInPalette(
    generatorViewModel: GeneratorViewModel,
    color: com.example.palletify.data.Palette.Color,
    heightPerColor: Dp,
    onItemClick: (com.example.palletify.data.Palette.Color) -> Unit
) {
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val luminosity =
        calculateLuminosity(color.rgb.r, color.rgb.g, color.rgb.b)
    TrademarkComponentWrapper(
        color = color,
        list = generatorViewModel.trademarkedColor.hexes,
        map = generatorViewModel.trademarkedColor.colorsMap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightPerColor)
                .background(hexToComposeColor(color.hex))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Column(
                modifier = Modifier
                    .clickable { onItemClick(color) }
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 2.dp),
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerationModeDropdown(generatorViewModel: GeneratorViewModel) {
    val context = LocalContext.current
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val generationModes = GenerationMode.entries.toTypedArray()

    var expanded by remember { mutableStateOf(false) }
    // start with analogic by default
    var selectedMode by remember { mutableStateOf(GenerationMode.ANY.name) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = "Mode: $selectedMode",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            generationModes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(text = mode.name) },
                    onClick = {
                        selectedMode = mode.name
                        expanded = false
                        generatorViewModel.handleNewGenerationMode(mode)
                    }
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
