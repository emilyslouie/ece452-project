package com.example.palletify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Palette::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class PaletteDatabase : RoomDatabase() {
    abstract fun paletteDao(): PaletteDao

    companion object {
        @Volatile
        private var instance: PaletteDatabase? = null

        fun getDatabase(context: Context): PaletteDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    PaletteDatabase::class.java,
                    "palette_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                return newInstance
            }
        }
    }
}