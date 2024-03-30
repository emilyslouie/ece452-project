package com.example.palletify.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaletteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PaletteInterface

    private val _allPalettes = MutableStateFlow<List<Palette>>(emptyList())
    val getAllPalettes: StateFlow<List<Palette>> = _allPalettes

    init {
        val paletteDao = PaletteDatabase.getDatabase(application).paletteDao()
        repository = PaletteInterface(paletteDao)


        viewModelScope.launch {
            repository.getAllPalettes.collect {
                _allPalettes.value = it
            }
        }
    }

    fun addPalette(palette: Palette) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPalette(palette)
        }
    }
    fun deletePalette(palette: Palette) {
        viewModelScope.launch {
            repository.deletePalette(palette)
        }
    }

    fun updatePaletteColorsById(id: Int, newColors: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateColorsById(id, newColors)
        }
    }

}