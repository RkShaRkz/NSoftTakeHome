package com.nsoft.github.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nsoft.github.data.local.room.FAVORITES_PRIMARY_KEY
import com.nsoft.github.data.local.room.FAVORITES_TABLE_NAME
import com.nsoft.github.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Avoid duplicates
    fun addFavorite(favorite: Favorite)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM ${FAVORITES_TABLE_NAME} WHERE ${FAVORITES_PRIMARY_KEY} = :repoId)"
    )
    fun isFavorite(repoId: Long): Flow<Boolean>

    @Delete
    fun removeFavorite(favorite: Favorite)

    @Query("SELECT * FROM ${FAVORITES_TABLE_NAME}")
    fun getAllFavorites(): Flow<List<Favorite>>

    // now the suspend ones
    @Query(
        "SELECT EXISTS(SELECT 1 FROM ${FAVORITES_TABLE_NAME} WHERE ${FAVORITES_PRIMARY_KEY} = :repoId)"
    )
    suspend fun isFavoriteSuspend(repoId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Avoid duplicates
    suspend fun addFavoriteSuspend(favorite: Favorite)

    @Delete
    suspend fun removeFavoriteSuspend(favorite: Favorite)

    @Query("SELECT * FROM ${FAVORITES_TABLE_NAME}")
    suspend fun getAllFavoritesSuspend(): List<Favorite>
}
