package com.example.palletify.database

import kotlinx.coroutines.flow.Flow


class PaletteRepository(private val paletteDao: PaletteDao) {
    val getAllPalettes: Flow<List<Palette>> = paletteDao.getAllPalettes()

    suspend fun addPalette(palette: Palette){
        paletteDao.insertPalette(palette)
    }
}