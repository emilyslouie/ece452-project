package com.example.palletify.ui.generator

import androidx.lifecycle.ViewModel
import com.example.palletify.data.fetchPalette
import com.example.palletify.data.fetchRandomHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread

/**
 * ViewModel containing the app data and methods to process the data for the generator
 */
class GeneratorViewModel : ViewModel() {

    // Generator UI state
    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()


    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    init {
        thread {
            resetPalette()
        }
    }

    /*
    * Re-initializes the palette when first loading the generator
    */
    fun resetPalette() {
        usedSeedColors.clear();
        getRandomPalette();

    }

    /*
    * Generates a random palette based off a random color
    */
    fun getRandomPalette() {
        val randomHexResponse = fetchRandomHex();
        return if (usedSeedColors.contains(randomHexResponse)) {
            getRandomPalette();
        } else {
            usedSeedColors.add(randomHexResponse);
            val palette = fetchPalette(randomHexResponse);
            _uiState.update { currentState ->
                currentState.copy(
                    numberOfColours = palette.count,
                    colors = palette.colors,
                    mode = palette.mode,
                    image = palette.image
                )
            }

        }
    }
}
