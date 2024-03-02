package com.example.palletify.ui.generator

import com.example.palletify.data.Palette.Color
import com.example.palletify.data.Palette.Image

/**
 * Data class that represents the generator's UI state
 */
data class GeneratorUiState(
    val numberOfColours: Int = 5,
    val colors: List<Color> = emptyList(),
    val mode: String = "monochrome",
    val image: Image = Image("", ""),
    val lockedColors: List<Color> = emptyList()
)