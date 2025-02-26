package com.nsoft.github.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nsoft.github.data.local.room.REPOSITORIES_TABLE_NAME
import com.nsoft.github.domain.model.GitRepository
import kotlinx.coroutines.flow.Flow

@Dao
interface GitRepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gitRepository: GitRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repositories: List<GitRepository>)

    @Query("SELECT * FROM ${REPOSITORIES_TABLE_NAME}")
    suspend fun getAllRepositories(): List<GitRepository>

    @Query("SELECT * FROM ${REPOSITORIES_TABLE_NAME}")
    fun getAllRepositoriesFlow(): Flow<List<GitRepository>>
}
