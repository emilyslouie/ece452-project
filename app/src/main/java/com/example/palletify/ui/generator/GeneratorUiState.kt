package com.example.palletify.ui.generator

import com.example.palletify.data.GenerationMode
import com.example.palletify.data.Palette

/**
 * Data class that represents the generator's UI state
 */
data class GeneratorUiState(
    val numberOfColours: Int = 5,
    val currentPalette: MutableList<Palette.Color> = mutableListOf(),
    val mode: GenerationMode = GenerationMode.ANY,
    val lockedColors: MutableSet<Palette.Color> = mutableSetOf(),
    // Maintain a count for palettes in each undo/redo stack since the UI doesn't need the entire stack
    var palettesInUndoStack: Int = 0,
    var palettesInRedoStack: Int = 0,
)