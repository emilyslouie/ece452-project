package com.example.palletify.ui.this_or_that

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.palletify.database.PaletteViewModel
import com.example.palletify.ui.components.TrademarkComponentWrapper
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
                            .padding(top = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        // This button
                        Button(onClick = {
                            thread { thisOrThatViewModel.getNewPalette(0) }
                        }) {
                            Text(
                                text = "This",
                                style = typography.titleLarge
                            )
                        }
                        Text(text = "or")
                        // That button
                        Button(onClick = {
                            thread { thisOrThatViewModel.getNewPalette(1) }
                        }) {
                            Text(
                                text = "That",
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
                        thisOrThatUiState.currentPalettes.forEach { palette ->
                            Column(
                                Modifier
                                    .fillMaxHeight()
                            ) {
                                Palette(
                                    thisOrThatViewModel = thisOrThatViewModel,
                                    colors = palette.colors,
                                    numOfColors = palette.numberOfColours,
                                    heightAvailable = columnHeightDp
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun Palette(
    thisOrThatViewModel: ThisOrThatViewModel,
    colors: MutableList<com.example.palletify.data.Palette.Color>,
    numOfColors: Int,
    heightAvailable: Dp
) {
    val heightPerColor = heightAvailable / numOfColors;
    colors.forEach { color ->
        ColorInPalette(
            thisOrThatViewModel,
            color,
            heightPerColor
        )
    }

}

@Composable
fun ColorInPalette(
    thisOrThatViewModel: ThisOrThatViewModel,
    color: com.example.palletify.data.Palette.Color,
    heightPerColor: Dp
) {
    val thisOrThatUiState by thisOrThatViewModel.uiState.collectAsStateWithLifecycle()
    TrademarkComponentWrapper(
        color = color,
        list = thisOrThatViewModel.trademarkedColor.hexes,
        map = thisOrThatViewModel.trademarkedColor.colorsMap
    ) {
        Row(
            modifier = Modifier
                // TODO: cannot figure out how to make each palette be half of the screen
                .fillMaxWidth(0.5f)
                .height(heightPerColor)
                .background(hexToComposeColor(color.hex.value.toString()))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

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
