package com.example.palletify.database

import kotlinx.coroutines.flow.Flow


class PaletteInterface(private val paletteDao: PaletteDao) {
    val getAllPalettes: Flow<List<Palette>> = paletteDao.getAllPalettes()

    suspend fun addPalette(palette: Palette) {
        paletteDao.insertPalette(palette)
    }

    suspend fun deletePalette(palette: Palette) {
        paletteDao.deletePalette(palette)
    }

    suspend fun updateColorsById(id: Int, colors: List<String>) {
        paletteDao.updateColorsById(id, colors)
    }
}

