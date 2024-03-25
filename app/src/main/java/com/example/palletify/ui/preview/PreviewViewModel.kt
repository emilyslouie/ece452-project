package com.example.palletify.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import com.example.palletify.data.Palette
import com.example.palletify.data.fetchPalette
import com.example.palletify.data.fetchRandomHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread

/**
 * ViewModel containing the app data and methods to process the data for the preview
 */
class PreviewViewModel: ViewModel() {

    // Generator UI state
    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    init {
        thread {
            setPreviewTestData()
        }
    }

    fun setPreviewTestData() {
        val randomHexResponse = fetchRandomHex();
        val palette = fetchPalette(randomHexResponse);
        _uiState.update { currentState ->
            currentState.copy(
                colors = palette.colors,
                currentColor = palette.colors[0]
            )
        }
    }

    fun setCurrentColor(color: Palette.Color) {
        _uiState.update { currentState ->
            currentState.copy(
                currentColor = color
            )
        }
    }
}
