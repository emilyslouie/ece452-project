package com.example.palletify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaletteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPalette(palette: Palette)

    @Delete
    suspend fun deletePalette(palette: Palette)

    @Query("SELECT * FROM palette ORDER BY id ASC")
    fun getAllPalettes(): Flow<List<Palette>>
}