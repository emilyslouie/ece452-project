package com.example.palletify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListToString(list: List<String>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun fromStringToList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
}


@Entity
data class Palette(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "number_of_colors") val numberOfColors: Int?,
    @ColumnInfo(name = "colors") val colors: List<String>?,
    @ColumnInfo(name = "mode") val mode: String?,
    @ColumnInfo(name = "favourite") val favourite: Boolean?
)
