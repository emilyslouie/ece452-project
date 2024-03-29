package com.example.palletify.ui.preview

import com.example.palletify.data.Palette
import com.example.palletify.data.Palette.Hex

/**
 * Data class that represents the preview's UI state
 */
data class PreviewUiState(
    val colors: MutableList<Palette.Color> = mutableListOf(),
    val currentColor: Palette.Color = Palette.Color(
        Hex("#FFFFFF", "FFFFFF"),
        Palette.Rgb(255, 255, 255),
        Palette.Name("white")
    )
)