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
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // The reasoning for the following change isn't as subtle or short ...
    // The problem we're solving is that, the "Favorites" table wasn't being persisted across restarts
    //
    // Since we're fetching new API results on every app restart, and REPLACE essentially
    // performs a DELETE followed by an INSERT, the DELETE triggers the FavoriteDao's CASCADE
    // which will also clear the linked favorites from their table once the "git_repository" entries
    // have been replaced (deleted and then inserted)
    //
    // So, we're gonna do something different. We will INSERT or UPDATE depending on whether the row
    // exists in the table in the repo layer, and just not use REPLACE here. That's why we're using IGNORE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gitRepository: GitRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repositories: List<GitRepository>)

    @Query("SELECT * FROM ${REPOSITORIES_TABLE_NAME}")
    suspend fun getAllRepositories(): List<GitRepository>

    @Query("SELECT * FROM ${REPOSITORIES_TABLE_NAME}")
    fun getAllRepositoriesFlow(): Flow<List<GitRepository>>
}
