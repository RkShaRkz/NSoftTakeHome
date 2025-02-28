package com.nsoft.github.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nsoft.github.data.local.room.COLLABORATORS_PRIMARY_KEY
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

    @Query("DELETE FROM ${COLLABORATORS_TABLE_NAME} WHERE ${COLLABORATORS_PRIMARY_KEY} = :login")
    suspend fun removeByLogin(login: String): Int

    @Query("DELETE FROM ${COLLABORATORS_TABLE_NAME} WHERE ${COLLABORATORS_PRIMARY_KEY} IN (:loginList)")
    suspend fun removeAllByLogin(loginList: List<String>): Int

    @Query(
        "SELECT EXISTS(SELECT 1 FROM $COLLABORATORS_TABLE_NAME WHERE $COLLABORATORS_PRIMARY_KEY = :login)"
    )
    fun getCollaboratorByLoginFlow(login: String): Flow<Boolean>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM $COLLABORATORS_TABLE_NAME WHERE $COLLABORATORS_PRIMARY_KEY = :login)"
    )
    suspend fun getCollaboratorByLoginSuspend(login: String): Boolean

    @Query(
        "SELECT EXISTS(SELECT 1 FROM $COLLABORATORS_TABLE_NAME WHERE ${COLLABORATORS_PRIMARY_KEY} = :login)"
    )
    fun doesCollaboratorExistsFlow(login: String): Flow<Boolean>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM $COLLABORATORS_TABLE_NAME WHERE ${COLLABORATORS_PRIMARY_KEY} = :login)"
    )
    suspend fun doesCollaboratorExistsSuspend(login: String): Boolean
}
