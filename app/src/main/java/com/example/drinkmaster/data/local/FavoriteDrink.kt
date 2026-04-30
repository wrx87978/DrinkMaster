package com.example.drinkmaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteDrink(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val thumbnailUrl: String?,
    val rating: Int = 0,
    val note: String? = null
)
