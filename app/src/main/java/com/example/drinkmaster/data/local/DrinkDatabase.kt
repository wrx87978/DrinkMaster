package com.example.drinkmaster.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteDrink::class], version = 1, exportSchema = false)
abstract class DrinkDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile private var INSTANCE: DrinkDatabase? = null

        fun getInstance(context: Context): DrinkDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DrinkDatabase::class.java,
                    "drink_database"
                ).build().also { INSTANCE = it }
            }
    }
}
