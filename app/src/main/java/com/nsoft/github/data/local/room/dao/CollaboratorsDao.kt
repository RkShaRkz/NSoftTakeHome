package com.nsoft.github.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nsoft.github.data.local.room.COLLABORATORS_TABLE_NAME
import com.nsoft.github.domain.model.GitCollaborator
import kotlinx.coroutines.flow.Flow

@Dao
interface CollaboratorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gitCollaborator: GitCollaborator)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gitCollaboratorList: List<GitCollaborator>)

    @Query("SELECT * FROM ${COLLABORATORS_TABLE_NAME}")
    suspend fun getAllCollaborators(): List<GitCollaborator>

    @Query("SELECT * FROM ${COLLABORATORS_TABLE_NAME}")
    fun getAllCollaboratorsFlow(): Flow<List<GitCollaborator>>

    @Delete
    fun remove(gitCollaborator: GitCollaborator)

    @Delete
    fun removeAll(gitCollaboratorList: List<GitCollaborator>)
}
