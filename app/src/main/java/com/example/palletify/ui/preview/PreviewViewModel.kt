package com.example.palletify.ui.preview

import androidx.lifecycle.ViewModel
import com.example.palletify.data.Palette
import com.example.palletify.data.fetchPaletteFromColorApi
import com.example.palletify.data.fetchRandomColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread

/**
 * ViewModel containing the app data and methods to process the data for the preview
 */
class PreviewViewModel : ViewModel() {

    // Generator UI state
    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    init {
        thread {
            setPreviewTestData()
        }
    }

    private fun setPreviewTestData() {
        val randomHexResponse = fetchRandomColors();
        val palette = fetchPaletteFromColorApi(
            randomHexResponse[0].hex
                .clean
        );
        _uiState.update { currentState ->
            currentState.copy(
                colors = palette,
                currentColor = palette[0]
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
