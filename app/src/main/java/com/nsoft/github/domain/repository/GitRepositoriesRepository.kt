package com.nsoft.github.domain.repository

import com.nsoft.github.domain.model.GitRepository
import kotlinx.coroutines.flow.Flow

interface GitRepositoriesRepository {

    fun getAllRepositories(): Flow<List<GitRepository>>

    suspend fun getAllRepositoriesSuspend(): List<GitRepository>

    fun isRepositoryFavorited(gitRepository: GitRepository): Flow<Boolean>

    suspend fun isRepositoryFavoritedSuspend(gitRepository: GitRepository): Boolean

    fun setRepositoryFavorited(gitRepository: GitRepository, newFavoriteStatus: Boolean)

    suspend fun setRepositoryFavoritedSuspend(gitRepository: GitRepository, newFavoriteStatus: Boolean)

    fun addRepositories(repoList: List<GitRepository>)

    suspend fun addRepositoriesSuspend(repoList: List<GitRepository>)

    fun toggleRepositoryFavoriteStatus(repo: GitRepository)

    suspend fun toggleRepositoryFavoriteStatusSuspend(repo: GitRepository)
}
