package com.example.palletify.ui.preview

import androidx.lifecycle.ViewModel
import com.example.palletify.data.Palette
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
        }
    }

    fun setDefaultValues() {
        _uiState.update {currentState ->
            currentState.copy(
                palette = listOf<String>("#000000", "#000000", "#000000", "#000000", "#000000"),
                currentColor = "#000000"
            )
        }
    }

    fun setBuildMode(x: Boolean) {
        _uiState.update {currentState ->
            currentState.copy(
                buildMode = x
            )
        }
    }

    fun setCurrentPaletteID(id: Int) {
        _uiState.update {currentState ->
            currentState.copy(
                paletteID = id
            )
        }
    }

    fun setCurrentPalette(palette: List<String>) {
        _uiState.update { currentState ->
            currentState.copy(
                palette = palette
            )
        }
    }

    fun setCurrentColor(color: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentColor = color
            )
        }
    }
}
