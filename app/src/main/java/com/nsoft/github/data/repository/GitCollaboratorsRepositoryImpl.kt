package com.nsoft.github.data.repository

import com.nsoft.github.data.local.room.dao.CollaboratorsDao
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.repository.GitCollaboratorsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GitCollaboratorsRepositoryImpl @Inject constructor(
    private val collaboratorsDao: CollaboratorsDao
): GitCollaboratorsRepository {
    override fun getAllFavoriteCollaborators(): Flow<List<GitCollaborator>> {
        return collaboratorsDao.getAllCollaboratorsFlow()
    }

    override suspend fun getAllFavoriteCollaboratorsSuspend(): List<GitCollaborator> {
        return collaboratorsDao.getAllCollaborators()
    }

    override fun isCollaboratorFavorited(gitCollaborator: GitCollaborator): Flow<Boolean> {
        return collaboratorsDao
            .doesCollaboratorExistsFlow(gitCollaborator.login)
    }

    override suspend fun isCollaboratorFavoritedSuspend(gitCollaborator: GitCollaborator): Boolean {
        return collaboratorsDao.doesCollaboratorExistsSuspend(gitCollaborator.login)
    }

    override fun addFavoriteCollaborators(collaboratorList: List<GitCollaborator>) {
        CoroutineScope(Dispatchers.IO).launch {
            collaboratorsDao.insertAll(collaboratorList)
        }
    }

    override suspend fun addFavoriteCollaboratorsSuspend(collaboratorList: List<GitCollaborator>) {
        collaboratorsDao.insertAll(collaboratorList)
    }

    override fun addFavoriteCollaborator(collaborator: GitCollaborator) {
        CoroutineScope(Dispatchers.IO).launch {
            addFavoriteCollaboratorSuspend(collaborator)
        }
    }

    override suspend fun addFavoriteCollaboratorSuspend(collaborator: GitCollaborator) {
        collaboratorsDao.insert(collaborator)
    }

    override fun toggleCollaboratorFavoriteStatus(collaborator: GitCollaborator) {
        CoroutineScope(Dispatchers.IO).launch {
            toggleCollaboratorFavoriteStatusSuspend(collaborator)
        }
    }

    override suspend fun toggleCollaboratorFavoriteStatusSuspend(collaborator: GitCollaborator) {
        // This one is easy. If collaborator is already 'favored' (meaning he's in the database)
        // we'll remove him, otherwise we'll just add him. This one is simpler than what we had in
        // the GitReposRepositories
        val isItemFavorite = isCollaboratorFavoritedSuspend(collaborator)
        if (isItemFavorite) {
            // remove - since removal returns an int, we can keep it unused for debugging purposes
            val removedRows = collaboratorsDao.removeByLogin(collaborator.login)
        } else {
            // add - since adding *does not* return anything, we don't need to keep the retVal
            collaboratorsDao.insert(collaborator)
        }
    }
}
