package com.nsoft.github.data.repository

import androidx.compose.ui.util.fastAny
import com.nsoft.github.data.local.room.dao.FavoriteRepositoryDao
import com.nsoft.github.data.local.room.dao.GitRepositoryDao
import com.nsoft.github.domain.model.FavoriteRepository
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.util.FuzzyFilterer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class GitRepositoriesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoriteRepositoryDao,
    private val gitRepositoryDao: GitRepositoryDao,
    private val textFilterer: FuzzyFilterer
): GitRepositoriesRepository {
    override fun getAllRepositories(): Flow<List<GitRepository>> {
        return gitRepositoryDao.getAllRepositoriesFlow()
    }

    override suspend fun getAllRepositoriesSuspend(): List<GitRepository> {
        return gitRepositoryDao.getAllRepositories()
    }

    override fun isRepositoryFavorited(gitRepository: GitRepository): Flow<Boolean> {
        return favoritesDao.isFavoriteRepository(gitRepository.id)
    }

    override suspend fun isRepositoryFavoritedSuspend(gitRepository: GitRepository): Boolean {
        return favoritesDao.isFavoriteRepositorySuspend(gitRepository.id)
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
                favoritesDao.addFavoriteRepositorySuspend(FavoriteRepository(gitRepository.id))
            }
        } else {
            // Since we need to remove from favorites, check the current status
            if (isCurrentlyFavorite) {
                // remove
                favoritesDao.removeFavoriteRepositorySuspend(FavoriteRepository(gitRepository.id))
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
        // First, grab all existing repos
        val currentRepos = gitRepositoryDao.getAllRepositories()

        // Split the argument into two lists, one for insertion and one for updating
        val updateRepos = repoList
            .filter { repo ->
                currentRepos.fastAny { it.id == repo.id }
            }
        val insertRepos = repoList.filter { it !in updateRepos }

        // Now that we have our two spliced lists, lets update all existing and insert new ones
        gitRepositoryDao.updateAll(updateRepos)
        gitRepositoryDao.insertAll(insertRepos)
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

    override fun getAllRepositoriesFiltered(filterCriteria: String): Flow<List<GitRepository>> {
        return getAllRepositories()
            .map { repoList ->
                // Now, reduce the list of git repos
                repoList.filter { gitRepo ->
                    textFilterer.matchesFuzzySearch(gitRepo.repoName, filterCriteria)
                }
            }
            // Now, filter out the empty lists
            .filter { list -> list.isNotEmpty() }
    }

    override suspend fun getAllRepositoriesFilteredSuspend(filterCriteria: String): List<GitRepository> {
        val filteredRepos = getAllRepositoriesSuspend().filter { item ->
            val match = textFilterer.matchesFuzzySearch(item.repoName, filterCriteria)
//            MyLogger.d("SHARK", "text filter matches ? ${match}")

            match
        }

//        MyLogger.d("SHARK", "[IN REPO] filtered repos count: ${filteredRepos.size}\tfiltered repos: ${filteredRepos}")

        return filteredRepos
    }
}
