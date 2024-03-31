package com.example.palletify.ui.generator

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import java.util.Collections
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

    private val _colorPalette: MutableState<Map<String, String>> = mutableStateOf(mapOf())
    val colorPalette: State<Map<String, String>> = _colorPalette
    val trademarkedColor = TrademarkedColor();

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

    private fun getPalette(): PaletteObj {
        // When we undo to an old palette, that may have had different locked colours
        val lockedColors = currentPalette.lockedColours;
        val count = uiState.value.numberOfColours;
        var mode = uiState.value.mode;

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
        thread {
            if (currentPalette.numberOfColours < MAX_NUMBER_OF_COLORS) {
                val newCount = currentPalette.numberOfColours + 1;
                currentPalette.numberOfColours = newCount;
                val newColor = getColorMatchingPalette(seed, currentPalette.mode);
                val indexToAddNewColor = currentPalette.colors.indexOf(seed) + 1;
                newColor.index = indexToAddNewColor
                currentPalette.colors.add(indexToAddNewColor, newColor);
                val newPalette = currentPalette.colors.toMutableList();
                _uiState.update { currentState ->
                    currentState.copy(
                        numberOfColours = newCount,
                        currentPalette = newPalette
                    )
                }
            }
        }

    }

    /*
    * Handle decrease number of colors shown in the palette
    */
    fun handleDecreaseNumOfColors(color: Palette.Color) {
        if (currentPalette.numberOfColours > MIN_NUMBER_OF_COLORS) {
            val newCount = currentPalette.numberOfColours - 1;
            currentPalette.numberOfColours = newCount
            currentPalette.colors.remove(color);
            val newPalette = currentPalette.colors.toMutableList();
            _uiState.update { currentState ->
                currentState.copy(
                    numberOfColours = newCount,
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

    /*
    * Set colors in color palette based off an image
    */
    fun setColorPaletteFromImage(colors: Map<String, String>) {
        _colorPalette.value = colors
    }

    /*
    * Update the generation mode
    */
    fun handleNewGenerationMode(newMode: GenerationMode) {
        _uiState.update { currentState ->
            currentState.copy(
                mode = newMode
            );
        }
    }

    fun replaceColorInPalette(color: Palette.Color, colorToReplace: Palette.Color) {
        val newPalette = currentPalette.colors;
        val indexToReplace = newPalette.indexOf(colorToReplace);
        if (indexToReplace == -1) return;
        newPalette[indexToReplace] = color;
        setCurrentPalette(
            PaletteObj(
                currentPalette.numberOfColours,
                newPalette,
                currentPalette.mode,
                currentPalette.lockedColours
            )
        )
    }

    /*
    * Move a colour "up" in the Palette's color list
    */
    fun handleMoveColourUp(color: Palette.Color) {
        val reorderedColours = currentPalette.colors;
        // swap with colour in list above it
        Collections.swap(reorderedColours, color.index, color.index - 1)
        setCurrentPalette(
            PaletteObj(
                currentPalette.numberOfColours,
                reorderedColours,
                currentPalette.mode,
                currentPalette.lockedColours
            )
        )
    }

    /*
    * Move a colour "down" in the Palette's color list
    */
    fun handleMoveColourDown(color: Palette.Color) {
        val reorderedColours = currentPalette.colors;
        // swap with colour in list above it
        Collections.swap(reorderedColours, color.index, color.index + 1)
        setCurrentPalette(
            PaletteObj(
                currentPalette.numberOfColours,
                reorderedColours,
                currentPalette.mode,
                currentPalette.lockedColours
            )
        )
    }
}
