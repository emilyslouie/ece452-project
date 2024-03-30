package com.example.palletify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaletteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPalette(palette: Palette)

    @Delete
    suspend fun deletePalette(palette: Palette)

    @Query("UPDATE palette SET colors = :colors WHERE id = :id")
    suspend fun updateColorsById(id: Int, colors: List<String>)

    @Query("SELECT * FROM palette ORDER BY id ASC")
    fun getAllPalettes(): Flow<List<Palette>>
}