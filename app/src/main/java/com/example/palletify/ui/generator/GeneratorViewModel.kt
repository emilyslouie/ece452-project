package com.example.palletify.ui.generator

import androidx.lifecycle.ViewModel
import com.example.palletify.data.GenerationMode
import com.example.palletify.data.Palette
import com.example.palletify.data.fetchPalette
import com.example.palletify.data.fetchRandomColors
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
        val colors: MutableList<Palette.Color>,
        val mode: GenerationMode,
        // Locked colours are a member of PaletteObj so they will persist across undo/redo actions
        // We can change this later if it's undesirable behaviour
        val lockedColours: MutableSet<Palette.Color>,
    )

    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    // Current palette
    private var currentPalette: PaletteObj =
        PaletteObj(0, mutableListOf(), GenerationMode.ANY, mutableSetOf());

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
        getNewPalette();
    }

    fun getPalette(): PaletteObj {
        val lockedColors = uiState.value.lockedColors.toMutableSet();
        val count = uiState.value.numberOfColours;
        var mode = GenerationMode.RANDOM;

        // If the mode ANY, get a random mode
        if (mode == GenerationMode.ANY) {
            mode = getRandomGenerationMode();
        }

        val seeds = lockedColors.toMutableSet();

        // if there are no locked colors, then get a random color as the seed
        if (lockedColors.isEmpty()) {
            val randomColor = getRandomColor();
            seeds.add(randomColor);
            usedSeedColors.add(randomColor.hex.clean);
        }
        val colors = fetchPalette(
            seeds,
            count,
            mode
        );

        // For now, reset the mode to ANY, change this once we have a picker
        mode = GenerationMode.ANY;

        return PaletteObj(count, colors, mode, lockedColors);
    }

    /*
    * Generates a random hex code
    */
    private fun getRandomColor(): Palette.Color {
        val response = fetchRandomColors();
        return if (usedSeedColors.contains(response[0].hex.clean)) {
            getRandomColor();
        } else {
            return response[0];
        }
    }

    /*
    * Updates the current palette in the ViewModel and UiState
    */
    private fun setCurrentPalette(palette: PaletteObj) {
        _uiState.update { currentState ->
            currentState.copy(
                numberOfColours = palette.numberOfColours,
                currentPalette = palette.colors.toMutableList(),
                mode = palette.mode,
                lockedColors = palette.lockedColours.toMutableSet(),
            )
        }
        currentPalette = palette;
    }

    /*
    * Gets a palette and updates the palette in ViewModel and UiState
    */
    fun getNewPalette() {
        val palette = getPalette();
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
            val newColors = listOf<Palette.Color>(
                Palette.Color(
                    Palette.Hex("#FFFFFF", "FFFFFF"),
                    Palette.Rgb(
                        255,
                        255,
                        255,
                    )
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
    fun handleLockForColor(color: Palette.Color) {
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
    fun handleUnlockForColor(color: Palette.Color) {
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
