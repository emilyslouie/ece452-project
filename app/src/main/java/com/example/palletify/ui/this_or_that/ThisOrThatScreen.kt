package com.example.palletify.ui.this_or_that

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.palletify.database.Palette
import com.example.palletify.database.PaletteViewModel
import com.example.palletify.ui.components.TrademarkComponentWrapper
import com.example.palletify.ui.generator.calculateLuminosity
import com.example.palletify.ui.theme.PalletifyTheme
import kotlin.concurrent.thread


@Composable
fun ThisOrThatScreen(thisOrThatViewModel: ThisOrThatViewModel = viewModel()) {
    val thisOrThatUiState by thisOrThatViewModel.uiState.collectAsStateWithLifecycle()

    val localDensity = LocalDensity.current
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }

    val context = LocalContext.current
    val paletteViewModel =
        ViewModelProvider(context as ViewModelStoreOwner)[PaletteViewModel::class.java]


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
                            .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // This button
                        Button(onClick = {
                            thread { thisOrThatViewModel.getNewPalette(0) }
                        }) {
                            Text(
                                text = "This",
                                style = typography.titleSmall
                            )
                        }
                        TextButton(
                            onClick = {
                                val currentPalette = thisOrThatViewModel.uiState.value.currentPalettes[0].colors
                                val numberOfColors = thisOrThatViewModel.uiState.value.currentPalettes[0].numberOfColours
                                val mode = thisOrThatViewModel.uiState.value.currentPalettes[0].mode
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
                                text = "Save",
                                style = typography.labelSmall
                            )
                        }
                        Text(text = "or")
                        // That button
                        Button(onClick = {
                            thread { thisOrThatViewModel.getNewPalette(1) }
                        }) {
                            Text(
                                text = "That",
                                style = typography.titleSmall
                            )
                        }
                        TextButton(
                            onClick = {
                                val currentPalette = thisOrThatViewModel.uiState.value.currentPalettes[1].colors
                                val numberOfColors = thisOrThatViewModel.uiState.value.currentPalettes[1].numberOfColours
                                val mode = thisOrThatViewModel.uiState.value.currentPalettes[1].mode
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
                                text = "Save",
                                style = typography.labelSmall
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
                    Row() {
                        if (thisOrThatUiState.currentPalettes.size > 0)
                            Palettes(thisOrThatViewModel, thisOrThatUiState.currentPalettes, columnHeightDp)
                    }
                }
            }
        }
    }
}

@Composable
fun Palettes(
    thisOrThatViewModel: ThisOrThatViewModel,
    currentPalettes: MutableList<ThisOrThatViewModel.PaletteObj>,
    heightAvailable: Dp
) {

    val numOfColors = currentPalettes[0].numberOfColours
    val heightPerColor = heightAvailable / numOfColors;
    Column () {
        for (i in 0..<numOfColors) {
            ColorsInRow(thisOrThatViewModel, currentPalettes[0].colors[i], currentPalettes[1].colors[i], heightPerColor)
        }
    }
}

@Composable
fun ColorsInRow(
    thisOrThatViewModel: ThisOrThatViewModel,
    firstColor: com.example.palletify.data.Palette.Color,
    secondColor: com.example.palletify.data.Palette.Color,
    heightPerColor: Dp
) {
    val thisOrThatUiState by thisOrThatViewModel.uiState.collectAsStateWithLifecycle()

    Row(
    ) {
        TrademarkComponentWrapper(
            color = firstColor,
            list = thisOrThatViewModel.trademarkedColor.hexes,
            map = thisOrThatViewModel.trademarkedColor.colorsMap
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(heightPerColor)
                    .background(hexToComposeColor(firstColor.hex.value.toString()))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val luminosity =
                    calculateLuminosity(firstColor.rgb.r, firstColor.rgb.g, firstColor.rgb.b)

                Column(
                    modifier = Modifier
                        .clickable { }
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 2.dp),
                        text = firstColor.hex.clean,
                        color = if (luminosity >= 0.179) Color.Black else Color.White,
                        style = typography.headlineSmall
                    )
                    Text(
                        text = firstColor.name.value,
                        color = if (luminosity >= 0.179) Color(35, 35, 35) else Color(230, 230, 230),
                        style = typography.labelMedium
                    )
                }
            }
        }

        TrademarkComponentWrapper(
            color = secondColor,
            list = thisOrThatViewModel.trademarkedColor.hexes,
            map = thisOrThatViewModel.trademarkedColor.colorsMap
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(heightPerColor)
                    .background(hexToComposeColor(secondColor.hex.value.toString()))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val luminosity =
                    calculateLuminosity(secondColor.rgb.r, secondColor.rgb.g, secondColor.rgb.b)

                Column(
                    modifier = Modifier
                        .clickable { }
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 2.dp),
                        text = secondColor.hex.clean,
                        color = if (luminosity >= 0.179) Color.Black else Color.White,
                        style = typography.headlineSmall
                    )
                    Text(
                        text = secondColor.name.value,
                        color = if (luminosity >= 0.179) Color(35, 35, 35) else Color(230, 230, 230),
                        style = typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThisOrThatScreenPreview() {
    PalletifyTheme {
        ThisOrThatScreen()
    }
}
