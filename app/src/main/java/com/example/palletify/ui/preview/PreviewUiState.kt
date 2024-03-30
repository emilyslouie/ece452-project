package com.example.palletify.ui.preview

import com.example.palletify.data.Palette
import com.example.palletify.data.Palette.Hex

/**
 * Data class that represents the preview's UI state
 */
data class PreviewUiState(
    var paletteID: Int = 0,
    var palette: List<String> = listOf<String>(),
    val currentColor: String = "#FFFFFF"
)