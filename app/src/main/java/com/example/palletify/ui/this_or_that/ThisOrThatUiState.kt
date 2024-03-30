package com.example.palletify.ui.this_or_that

/**
 * Data class that represents the This or That's UI state
 */
data class ThisOrThatUiState(
    val currentPalettes: MutableList<ThisOrThatViewModel.PaletteObj> = mutableListOf(),
)