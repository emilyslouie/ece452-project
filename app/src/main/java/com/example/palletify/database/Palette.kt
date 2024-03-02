package com.example.palletify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Palette(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "number_of_colors") val numberOfColors: Int?,
    @ColumnInfo(name = "color1") val color1: String?,
    @ColumnInfo(name = "color2") val color2: String?,
    @ColumnInfo(name = "color3") val color3: String?,
    @ColumnInfo(name = "color4") val color4: String?,
    @ColumnInfo(name = "color5") val color5: String?,
    @ColumnInfo(name = "mode") val mode: String?,
//    @ColumnInfo(name = "image") val image: Image?,
//    @ColumnInfo(name = "locked_colors") val lockedColors: List<Color>?,
    @ColumnInfo(name = "Favourite") val favourite: Boolean?
)