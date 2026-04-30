package com.example.drinkmaster.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY name ASC")
    fun getAll(): Flow<List<FavoriteDrink>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    fun getById(id: String): Flow<FavoriteDrink?>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drink: FavoriteDrink)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE favorites SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: String, rating: Int)

    @Query("UPDATE favorites SET note = :note WHERE id = :id")
    suspend fun updateNote(id: String, note: String?)

    @Query("UPDATE favorites SET folder = :folder WHERE id = :id")
    suspend fun updateFolder(id: String, folder: String?)

    @Query("SELECT * FROM favorites WHERE folder = :folder")
    fun getByFolder(folder: String): Flow<List<FavoriteDrink>>
}
