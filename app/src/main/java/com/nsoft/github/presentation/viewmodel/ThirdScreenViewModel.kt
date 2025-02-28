package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.model.ThirdScreenErrorState
import com.nsoft.github.domain.navigation.ThirdScreenNavigationEvent
import com.nsoft.github.domain.repository.GitCollaboratorsRepository
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThirdScreenViewModel @Inject constructor(
    private val gitReposRepository: GitRepositoriesRepository,
    private val collaboratorsRepository: GitCollaboratorsRepository
): BaseViewModel<ThirdScreenNavigationEvent, ThirdScreenErrorState>() {

    override fun initialNavigationStreamValue() = ThirdScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = ThirdScreenErrorState.NoError

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    var _repositoryListStream: MutableStateFlow<List<GitRepository>> =
        MutableStateFlow<List<GitRepository>>(
            emptyList()
        )
    val repositoryListStream: StateFlow<List<GitRepository>> = _repositoryListStream.asStateFlow()

    var _collaboratorsListStream: MutableStateFlow<List<GitCollaborator>> =
        MutableStateFlow<List<GitCollaborator>>(
            emptyList()
        )
    val collaboratorsListStream: StateFlow<List<GitCollaborator>> = _collaboratorsListStream.asStateFlow()

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
        when (index) {
            0 -> {
                // repos, refresh the git stream
                viewModelScope.launch {
                    gitReposRepository
                        .getAllRepositories()
                        .map { gitRepoList ->
                            gitRepoList.filter { gitRepo -> gitReposRepository.isRepositoryFavoritedSuspend(gitRepo) }
                        }
                        .collect { newRepoList ->
                            _repositoryListStream.value = newRepoList
                        }
                }
            }

            1 -> {
                // collaborators, refresh the collaborator stream
                viewModelScope.launch {
                    collaboratorsRepository
                        .getAllFavoriteCollaborators()
                        .map { collaboratorList ->
                            collaboratorList.filter { collaborator -> collaboratorsRepository.isCollaboratorFavoritedSuspend(collaborator) }
                        }
                        .collect { newCollaboratorList ->
                            _collaboratorsListStream.value = newCollaboratorList
                        }
                }
            }
        }
    }

    fun toggleRepositoryFavoriteStatus(gitRepo: GitRepository) {
        gitReposRepository.toggleRepositoryFavoriteStatus(gitRepo)
    }

    fun isFavoriteRepositoryFlow(gitRepo: GitRepository): Flow<Boolean> {
        return gitReposRepository.isRepositoryFavorited(gitRepo)
    }

    fun isFavoriteCollaborator(gitCollaborator: GitCollaborator): Flow<Boolean> {
        return collaboratorsRepository.isCollaboratorFavorited(gitCollaborator)
    }

    fun toggleCollaboratorFavoriteStatus(gitCollaborator: GitCollaborator) {
        collaboratorsRepository.toggleCollaboratorFavoriteStatus(gitCollaborator)
    }
}
