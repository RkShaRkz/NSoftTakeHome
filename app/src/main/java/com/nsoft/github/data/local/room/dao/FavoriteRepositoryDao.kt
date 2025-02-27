package com.nsoft.github.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nsoft.github.data.local.room.FAVORITES_PRIMARY_KEY
import com.nsoft.github.data.local.room.FAVORITES_TABLE_NAME
import com.nsoft.github.domain.model.FavoriteRepository
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRepositoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Avoid duplicates
    fun addFavoriteRepository(favoriteRepository: FavoriteRepository)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM ${FAVORITES_TABLE_NAME} WHERE ${FAVORITES_PRIMARY_KEY} = :repoId)"
    )
    fun isFavoriteRepository(repoId: Long): Flow<Boolean>

    @Delete
    fun removeFavoriteRepository(favoriteRepository: FavoriteRepository)

    @Query("SELECT * FROM ${FAVORITES_TABLE_NAME}")
    fun getAllFavoriteRepositories(): Flow<List<FavoriteRepository>>

    // now the suspend ones
    @Query(
        "SELECT EXISTS(SELECT 1 FROM ${FAVORITES_TABLE_NAME} WHERE ${FAVORITES_PRIMARY_KEY} = :repoId)"
    )
    suspend fun isFavoriteRepositorySuspend(repoId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Avoid duplicates
    suspend fun addFavoriteRepositorySuspend(favoriteRepository: FavoriteRepository)

    @Delete
    suspend fun removeFavoriteRepositorySuspend(favoriteRepository: FavoriteRepository)

    @Query("SELECT * FROM ${FAVORITES_TABLE_NAME}")
    suspend fun getAllFavoriteRepositoriesSuspend(): List<FavoriteRepository>
}
