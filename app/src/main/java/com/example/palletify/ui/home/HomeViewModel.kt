package com.example.palletify.ui.home

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import com.example.palletify.data.Color
import com.example.palletify.data.Image
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
class HomeViewModel : ViewModel() {

    // Generator UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    data class PaletteObj(
        val numberOfColours: Int,
        val colors: List<Color>,
        val mode: String,
        val image: Image,
    )

    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    // Current palette
    private var currentPalette: PaletteObj = PaletteObj(0, emptyList(), "", Image("", ""));

    // Stacks to keep palettes that can be undone and redone
    private var undoPalettes: ArrayDeque<PaletteObj> = ArrayDeque();
    private var redoPalettes: ArrayDeque<PaletteObj> = ArrayDeque();

    init {
        thread {
            resetPalette()
        }
    }

    /*
    * Re-initializes the palette when first loading the generator
    */
    private fun resetPalette() {
        usedSeedColors.clear();
        undoPalettes.clear();
        redoPalettes.clear();
        getNewRandomPalette();
    }

    /*
    * Generates a random palette based off a random color
    */
    private fun getRandomPalette(): PaletteObj {
        val randomHexResponse = getRandomHex();
        usedSeedColors.add(randomHexResponse);
        val response = fetchPalette(randomHexResponse);
        val newPalette = PaletteObj(response.count, response.colors, response.mode, response.image);
        return newPalette;
    }

    /*
    * Generates a random hex code
    */
    private fun getRandomHex(): String {
        val response: String = fetchRandomHex();
        return if (usedSeedColors.contains(response)) {
            getRandomHex();
        } else {
            return response;
        }
    }

    /*
    * Updates the current palette in the ViewModel and UiState
    */
    private fun setCurrentPalette(palette: PaletteObj) {
        _uiState.update { currentState ->
            currentState.copy(
                numberOfColours = palette.numberOfColours,
                currentPalette = palette.colors,
                mode = palette.mode,
                image = palette.image
            )
        }
        currentPalette = palette;
    }

    /*
    * Gets a random new palette, updates the current palette, and update undo/redo stacks
    */
    fun getNewRandomPalette() {
        val palette = getRandomPalette();
        var currentCountInUndoStack = _uiState.value.palettesInUndoStack;
        if (currentPalette.numberOfColours > 0) {
            undoPalettes.add(currentPalette);
            currentCountInUndoStack++;
        }
        setCurrentPalette(palette);
        // The redo stack is cleared if a new palette is generated
        redoPalettes.clear();
        _uiState.update { currentState ->
            currentState.copy(
                palettesInUndoStack = currentCountInUndoStack,
                palettesInRedoStack = 0
            );
        }
    }

    /*
    * Handle undo to go back to the previous palette
    */
    fun handleUndo() {
        // This is the only time when palettes are added to the redo stack
        redoPalettes.add(currentPalette);
        val oldPalette = undoPalettes.removeLast();
        setCurrentPalette(oldPalette);
        _uiState.update { currentState ->
            currentState.copy(
                palettesInUndoStack = currentState.palettesInUndoStack - 1,
                palettesInRedoStack = currentState.palettesInRedoStack + 1
            );
        }
    }


    /*
    * Handle redo to go to a palette previous palette
    */
    fun handleRedo() {
        undoPalettes.add(currentPalette);
        val newPalette = redoPalettes.removeLast();
        setCurrentPalette(newPalette);
        _uiState.update { currentState ->
            currentState.copy(
                palettesInUndoStack = currentState.palettesInUndoStack + 1,
                palettesInRedoStack = currentState.palettesInRedoStack - 1
            );
        }
    }

//    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri
//        imageUriState.value = uri
//
//    }
//
//    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        // Handle the returned Uri
//    }

}
