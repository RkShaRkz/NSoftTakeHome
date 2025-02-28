package com.nsoft.github.data.repository

import com.nsoft.github.data.local.room.dao.CollaboratorsDao
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.repository.GitCollaboratorsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        // This one is a bit more complicated; since Room doesn't quite have a "contains"
        // so we'll run the get 0 or 1, and decide based on the result
        return collaboratorsDao
            .getCollaboratorByIdFlow(gitCollaborator._databaseId)
            .map { gitCollaboratorItem -> if (gitCollaboratorItem != null) { true } else { false } }
    }

    override suspend fun isCollaboratorFavoritedSuspend(gitCollaborator: GitCollaborator): Boolean {
        val item = collaboratorsDao.getCollaboratorById(gitCollaborator._databaseId)
        return if (item != null) { true } else { false }
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
        // This one is easy. If collaborator is alreadfy 'favored' (meaning he's in the database)
        // we'll remove him, otherwise we'll just add him. This one is simpler than what we had in
        // the GitReposRepositories
        val isItemFavorite = isCollaboratorFavoritedSuspend(collaborator)
        if (isItemFavorite) {
            // remove
            collaboratorsDao.remove(collaborator)
        } else {
            // add
            collaboratorsDao.insert(collaborator)
        }
    }
}
