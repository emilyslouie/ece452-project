package com.example.palletify.ui.generator

import com.example.palletify.data.Color
import com.example.palletify.data.Image

/**
 * Data class that represents the generator's UI state
 */
data class GeneratorUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,

    val numberOfColours: Int = 5,
    val colors: List<Color> = emptyList(),
    val mode: String = "monochrome",
    val image: Image = Image("", ""),
    val lockedColors: List<Color> = emptyList()
)