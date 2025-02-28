package com.nsoft.github.domain.repository

import com.nsoft.github.domain.model.GitCollaborator
import kotlinx.coroutines.flow.Flow

interface GitCollaboratorsRepository {
    fun getAllFavoriteCollaborators(): Flow<List<GitCollaborator>>

    suspend fun getAllFavoriteCollaboratorsSuspend(): List<GitCollaborator>

    fun isCollaboratorFavorited(gitCollaborator: GitCollaborator): Flow<Boolean>

    suspend fun isCollaboratorFavoritedSuspend(gitCollaborator: GitCollaborator): Boolean

    fun addFavoriteCollaborators(collaboratorList: List<GitCollaborator>)

    suspend fun addFavoriteCollaboratorsSuspend(collaboratorList: List<GitCollaborator>)

    fun addFavoriteCollaborator(collaborator: GitCollaborator)

    suspend fun addFavoriteCollaboratorSuspend(collaborator: GitCollaborator)

    fun toggleCollaboratorFavoriteStatus(collaborator: GitCollaborator)

    suspend fun toggleCollaboratorFavoriteStatusSuspend(collaborator: GitCollaborator)
}
