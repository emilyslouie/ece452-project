package com.example.palletify.ui.preview

import com.example.palletify.data.Palette
import com.example.palletify.data.Palette.Hex

/**
 * Data class that represents the preview's UI state
 */
data class PreviewUiState(
    var buildMode: Boolean = false,
    var paletteID: Int = 0,
    var palette: List<String> = listOf<String>("#000000", "#000000", "#000000", "#000000", "#000000"),
    val currentColor: String = "#000000",
    val currentColorIndex: Int = 0
)