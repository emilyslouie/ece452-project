package com.example.palletify.ui.preview

import com.example.palletify.data.Palette.Color
import com.example.palletify.data.Palette.Hex
import com.example.palletify.data.Palette.Rgb
import com.example.palletify.data.Palette.RgBFraction
import com.example.palletify.data.Palette.Name
import com.example.palletify.data.Palette.Contrast

/**
 * Data class that represents the preview's UI state
 */
data class PreviewUiState(
    val test: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    val colors: List<Color> = emptyList(),
    val currentColor : Color = Color(Hex("#FFFFFF", "FFFFFF"), Rgb(RgBFraction(1F, 1F, 1F), 255F, 255F, 255F, "rgb(255, 255, 255)"), Name("white"), Contrast("#000000"))
)