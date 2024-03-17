package com.example.palletify.ui.generator

import androidx.lifecycle.ViewModel
import com.example.palletify.data.Palette
import com.example.palletify.data.Palette.Color
import com.example.palletify.data.fetchPalette
import com.example.palletify.data.fetchRandomHex
import com.example.palletify.data.getRandomGenerationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread


const val MAX_NUMBER_OF_COLORS = 6;
const val MIN_NUMBER_OF_COLORS = 3;

/**
 * ViewModel containing the app data and methods to process the data for the generator
 */
class GeneratorViewModel : ViewModel() {

    // Generator UI state
    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    data class PaletteObj(
        var numberOfColours: Int,
        val colors: MutableList<Color>,
        val mode: String,
        // Locked colours are a member of PaletteObj so they will persist across undo/redo actions
        // We can change this later if it's undesirable behaviour
        val lockedColours: MutableSet<Color>,
    )

    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    // Current palette
    private var currentPalette: PaletteObj =
        PaletteObj(0, mutableListOf(), "", mutableSetOf());

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
    private fun getRandomPalette(
        mode: String = getRandomGenerationMode(),
        numberOfColours: Int = 5
    ): PaletteObj {
        val randomHexResponse = getRandomHex();
        usedSeedColors.add(randomHexResponse);
        val response = fetchPalette(randomHexResponse, mode, numberOfColours);
        val newPalette = PaletteObj(
            response.count,
            response.colors,
            response.mode,
            mutableSetOf()
        );
        return newPalette;
    }

    /*
    * Generates a  palette based off a given seed
    */
    private fun getPaletteForSeed(seedColor: Color): PaletteObj {
        val seedHex = seedColor.hex.clean
        val response = fetchPalette(seedHex);
        val newPalette = PaletteObj(
            response.count,
            response.colors,
            response.mode,
            mutableSetOf()
        );

        // TODO: thecolorapi does not use the seed color in the new palette, so locking is unintuitive
        // it uses the locked colour as a seed, but it may not be present in the generated palette
        if (newPalette.colors.contains(seedColor)) {
            // Re-lock seed color (if it exists in palette) for consistency after generation
            currentPalette.lockedColours.add(seedColor)
            // Clone set to assign to ui state
            val newLockedColors = currentPalette.lockedColours.toMutableSet()

            _uiState.update { currentState ->
                currentState.copy(
                    // If we set lockedColors = currentPalette.lockedColours, then even though the contents
                    // of the set change, the ui state sees same reference address, so won't recompose
                    lockedColors = newLockedColors
                );
            }
        }
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
            )
        }
        currentPalette = palette;
    }

    /*
    * Gets a random new palette, updates the current palette, and update undo/redo stacks
    */
    fun getNewRandomPalette(
        mode: String = getRandomGenerationMode(),
        numberOfColours: Int = 5
    ) {
        val palette = getRandomPalette(mode, numberOfColours);
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
    * Gets a new palette using seed colour, updates the current palette, and update undo/redo stacks
    */
    fun getNewPaletteWithSeed(seedColor: Color) {
        val palette = getPaletteForSeed(seedColor);
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
        // Clone set to assign to ui state
        val oldLockedColors = currentPalette.lockedColours.toMutableSet()
        _uiState.update { currentState ->
            currentState.copy(
                palettesInUndoStack = currentState.palettesInUndoStack - 1,
                palettesInRedoStack = currentState.palettesInRedoStack + 1,
                lockedColors = oldLockedColors
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
        // Clone set to assign to ui state
        val newLockedColors = currentPalette.lockedColours.toMutableSet()
        _uiState.update { currentState ->
            currentState.copy(
                palettesInUndoStack = currentState.palettesInUndoStack + 1,
                palettesInRedoStack = currentState.palettesInRedoStack - 1,
                lockedColors = newLockedColors
            );
        }
    }

    /*
    * Handle increase number of colors shown in the palette
    */
    fun handleIncreaseNumOfColors(seed: Palette.Color) {
        if (currentPalette.numberOfColours < MAX_NUMBER_OF_COLORS) {
            currentPalette.numberOfColours++;
            val newColors = listOf<Color>(
                Color(
                    Palette.Hex("#FFFFFF", "FFFFFF"),
                    Palette.Rgb(
                        Palette.RgBFraction(1F, 1F, 1F),
                        255F,
                        255F,
                        255F,
                        "rgb(255, 255, 255)"
                    ),
                    Palette.Name("white"),
                    Palette.Contrast("#000000")
                )
            );
            val indexToAddNewColor = currentPalette.colors.indexOf(seed) + 1;
            currentPalette.colors.add(indexToAddNewColor, newColors[0]);
            val newPalette = currentPalette.colors.toMutableList();
            _uiState.update { currentState ->
                currentState.copy(
                    numberOfColours = currentPalette.numberOfColours,
                    currentPalette = newPalette
                )
            }
        }
    }

    /*
    * Handle decrease number of colors shown in the palette
    */
    fun handleDecreaseNumOfColors(color: Palette.Color) {
        if (currentPalette.numberOfColours > MIN_NUMBER_OF_COLORS) {
            currentPalette.numberOfColours--;
            currentPalette.colors.remove(color);
            val newPalette = currentPalette.colors.toMutableList();
            _uiState.update { currentState ->
                currentState.copy(
                    numberOfColours = currentPalette.numberOfColours,
                    currentPalette = newPalette
                )
            }
        }
    }

    /*
    * Add a colour to its palette's set of lockedColours
    */
    fun handleLockForColor(color: Color) {
        // Update view model
        currentPalette.lockedColours.add(color)
        // Clone set to assign to ui state
        val newLockedColors = currentPalette.lockedColours.toMutableSet()

        _uiState.update { currentState ->
            currentState.copy(
                // If we set lockedColors = currentPalette.lockedColours, then even though the contents
                // of the set change, the ui state sees same reference address, so won't recompose
                lockedColors = newLockedColors
            );
        }
    }

    /*
    * Remove a colour from its palette's set of lockedColours
    */
    fun handleUnlockForColor(color: Color) {
        // Update view model
        currentPalette.lockedColours.remove(color)
        // Clone set to assign to ui state
        val newLockedColors = currentPalette.lockedColours.toMutableSet()

        _uiState.update { currentState ->
            currentState.copy(
                // If we set lockedColors = currentPalette.lockedColours, then even though the contents
                // of the set change, the ui state sees same reference address, so won't recompose
                lockedColors = newLockedColors
            );
        }
    }
}
