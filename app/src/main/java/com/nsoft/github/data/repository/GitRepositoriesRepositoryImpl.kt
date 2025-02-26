package com.nsoft.github.data.repository

import com.nsoft.github.data.local.room.dao.FavoriteDao
import com.nsoft.github.data.local.room.dao.GitRepositoryDao
import com.nsoft.github.domain.model.Favorite
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GitRepositoriesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoriteDao,
    private val gitRepositoryDao: GitRepositoryDao
): GitRepositoriesRepository {
    override fun getAllRepositories(): Flow<List<GitRepository>> {
        return gitRepositoryDao.getAllRepositoriesFlow()
    }

    override suspend fun getAllRepositoriesSuspend(): List<GitRepository> {
        return gitRepositoryDao.getAllRepositories()
    }

    override fun isRepositoryFavorited(gitRepository: GitRepository): Flow<Boolean> {
        return favoritesDao.isFavorite(gitRepository.id)
    }

    override suspend fun isRepositoryFavoritedSuspend(gitRepository: GitRepository): Boolean {
        return favoritesDao.isFavoriteSuspend(gitRepository.id)
    }

    /**
     * Unlike the rest of the methods, this one will internally use [CoroutineScope] with [Dispatchers.IO]
     * to launch the suspend versions of the methods by invoking [setRepositoryFavoritedSuspend]
     */
    override fun setRepositoryFavorited(gitRepository: GitRepository, newFavoriteStatus: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            setRepositoryFavoritedSuspend(gitRepository, newFavoriteStatus)
        }
    }

    override suspend fun setRepositoryFavoritedSuspend(gitRepository: GitRepository, newFavoriteStatus: Boolean) {
        // First, depending on the new status, we may need to add, remove, or do nothing
        // First, check if it's favorited
        val isCurrentlyFavorite = isRepositoryFavoritedSuspend(gitRepository)

        if (newFavoriteStatus) {
            // since we need to mark it as 'favorite, check to see the current status
            if (isCurrentlyFavorite) {
                // Already favorite, do nothing
            } else {
                // Add to favorites
                favoritesDao.addFavoriteSuspend(Favorite(gitRepository.id))
            }
        } else {
            // Since we need to remove from favorites, check the current status
            if (isCurrentlyFavorite) {
                // remove
                favoritesDao.removeFavoriteSuspend(Favorite(gitRepository.id))
            } else {
                // already not favorite, do nothing
            }
        }
    }

    override fun addRepositories(repoList: List<GitRepository>) {
        CoroutineScope(Dispatchers.IO).launch {
            addRepositoriesSuspend(repoList)
        }
    }

    override suspend fun addRepositoriesSuspend(repoList: List<GitRepository>) {
        gitRepositoryDao.insertAll(repoList)
    }

    /**
     * This one also launches [toggleRepositoryFavoriteStatusSuspend] internally
     */
    override fun toggleRepositoryFavoriteStatus(repo: GitRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            toggleRepositoryFavoriteStatusSuspend(repo)
        }
    }

    override suspend fun toggleRepositoryFavoriteStatusSuspend(repo: GitRepository) {
        // get current status
        val currentFavStatus = isRepositoryFavoritedSuspend(repo)
        setRepositoryFavoritedSuspend(repo, !currentFavStatus)
    }
}
