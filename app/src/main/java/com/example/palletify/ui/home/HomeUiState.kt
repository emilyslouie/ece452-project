package com.example.palletify.ui.home

import com.example.palletify.data.Color
import com.example.palletify.data.Image

/**
 * Data class that represents the generator's UI state
 */
data class HomeUiState(
    val numberOfColours: Int = 5,
    val currentPalette: List<Color> = emptyList(),
    val mode: String = "monochrome",
    val image: Image = Image("", ""),
    val lockedColors: List<Color> = emptyList(),
    // Maintain a count for palettes in each undo/redo stack since the UI doesn't need the entire stack
    var palettesInUndoStack: Int = 0,
    var palettesInRedoStack: Int = 0,
)