package com.example.palletify.ui.generator

import com.example.palletify.data.Palette.Color
import com.example.palletify.data.Palette.Image

/**
 * Data class that represents the generator's UI state
 */
data class GeneratorUiState(
    val numberOfColours: Int = 5,
    val currentPalette: List<Color> = emptyList(),
    val mode: String = "monochrome",
    val image: Image = Image("", ""),
    val lockedColors: MutableSet<Color> = mutableSetOf(),
    // Maintain a count for palettes in each undo/redo stack since the UI doesn't need the entire stack
    var palettesInUndoStack: Int = 0,
    var palettesInRedoStack: Int = 0,
)