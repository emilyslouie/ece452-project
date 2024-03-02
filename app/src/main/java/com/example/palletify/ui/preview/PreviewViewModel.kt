package com.example.palletify.ui.preview

import androidx.lifecycle.ViewModel
import com.example.palletify.data.Palette.Color
import com.example.palletify.ui.preview.PreviewUiState
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


    // Set of colors that have already been used as a seed in the generator
    private var usedSeedColors: MutableSet<String> = mutableSetOf()

    init {
        thread {

        }
    }

    fun setTest(color: androidx.compose.ui.graphics.Color) {
        _uiState.update { currentState ->
            currentState.copy(
                test = color
            )
        }
    }

    fun setCurrentColor(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(
                currentColor = color
            )
        }
    }
}
