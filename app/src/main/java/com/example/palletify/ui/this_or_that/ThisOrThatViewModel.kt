package com.example.palletify.ui.this_or_that

import androidx.lifecycle.ViewModel
import com.example.palletify.data.GenerationMode
import com.example.palletify.data.Palette
import com.example.palletify.data.TrademarkedColor
import com.example.palletify.data.fetchPalette
import com.example.palletify.data.fetchRandomColors
import com.example.palletify.data.getColorMatchingPalette
import com.example.palletify.data.getRandomGenerationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread
import kotlin.random.Random


const val MAX_NUMBER_OF_COLORS = 6;
const val MIN_NUMBER_OF_COLORS = 3;

/**
 * ViewModel containing the app data and methods to process the data for the This or That Generator
 */
class ThisOrThatViewModel : ViewModel() {

    // This or That UI state
    private val _uiState = MutableStateFlow(ThisOrThatUiState())
    val uiState: StateFlow<ThisOrThatUiState> = _uiState.asStateFlow()

    data class PaletteObj(
        var numberOfColours: Int,
        val colors: MutableList<Palette.Color>,
        val mode: GenerationMode,
    )

    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    // Current palette
    private var currentPalettes = mutableListOf<PaletteObj>();

    val trademarkedColor = TrademarkedColor();

    init {
        thread {
            resetPalette()
        }
    }

    /*
    * Re-initializes the palettes when first loading the generator
    */
    private fun resetPalette() {
        usedSeedColors.clear();
        getNewPalettes();
    }

    private fun getPalette(
        mode: GenerationMode = GenerationMode.ANY,
        seed: Palette.Color = getRandomColor()
    ): PaletteObj {
        var currMode = mode;

        // If the mode ANY, get a random mode
        if (currMode == GenerationMode.ANY) {
            currMode = getRandomGenerationMode();
        }

        // TODO: if we have time change hardcode to allow for new colours to be added
        val colors = fetchPalette(
            mutableSetOf(seed),
            5,
            currMode
        );

        return PaletteObj(5, colors, mode);
    }

    fun getNewPalettes() {
        val palette1 = getPalette();
        val palette2 = getPalette();

        setCurrentPalettes(mutableListOf<PaletteObj>(palette1, palette2));
    }

    fun getNewPalette(paletteToKeep: Int) {
        val palette = uiState.value.currentPalettes[paletteToKeep];
        val mode = palette.mode;
        val randomIndex = Random.nextInt(0, palette.numberOfColours);
        val colorToMatch = palette.colors[randomIndex];

        val similarColor = getColorMatchingPalette(colorToMatch, mode);

        val newPalette = fetchPalette(mutableSetOf(similarColor), 5, mode);

        val indexToReplace = 1 - paletteToKeep

        setCurrentPalettes(mutableListOf(PaletteObj(5, newPalette, mode)), indexToReplace)
    }


    /*
    * Generates a random hex code
    */
    private fun getRandomColor(): Palette.Color {
        val response = fetchRandomColors();
        return if (usedSeedColors.contains(response[0].hex.clean)) {
            getRandomColor();
        } else {
            usedSeedColors.add(response[0].hex.clean)
            return response[0];
        }
    }

    /*
    * Updates the current palettes in the ViewModel and UiState
    */
    private fun setCurrentPalettes(
        palettes: MutableList<PaletteObj>,
        indexToReplace: Int = 999
    ) {
        // if we don't pass an argument in, then we need to replace the entire array
        if (indexToReplace > 1) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentPalettes = palettes.toMutableList(),
                )
            }
            currentPalettes = palettes;
        } else {
            // otherwise get the current palettes and replace the appropriate one
            val uiPalettes = _uiState.value.currentPalettes.toMutableList();
            uiPalettes[indexToReplace] = palettes[0];
            _uiState.update { currentState ->
                currentState.copy(
                    currentPalettes = uiPalettes.toMutableList(),
                )
            }
            currentPalettes = uiPalettes;
        }


    }


}
