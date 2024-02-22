package com.example.palletify.ui.generator

/**
 * Data class that represents the generator's UI state
 */
data class GeneratorUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,

)