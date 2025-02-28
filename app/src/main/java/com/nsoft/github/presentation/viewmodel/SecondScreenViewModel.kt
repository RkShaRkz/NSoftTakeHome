package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import com.nsoft.github.domain.repository.GitCollaboratorsRepository
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import com.nsoft.github.domain.usecase.GetCollaboratorsFromRepositoryDetailsUseCase
import com.nsoft.github.domain.usecase.params.CollaboratorType
import com.nsoft.github.domain.usecase.params.GetCollaboratorsFromRepositoryDetailsUseCaseParams
import com.nsoft.github.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondScreenViewModel @Inject constructor(
    private val gitReposRepository: GitRepositoriesRepository,
    private val collaboratorsRepository: GitCollaboratorsRepository,
    private val transitionalDataRepository: TransitionalDataRepository,
    private val getCollaboratorsFromRepositoryDetailsUseCase: GetCollaboratorsFromRepositoryDetailsUseCase
): BaseViewModel<SecondScreenNavigationEvent, SecondScreenErrorState>() {

    override fun initialNavigationStreamValue() = SecondScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = SecondScreenErrorState.NoError

    private var _contributorsListStream: MutableStateFlow<List<GitCollaborator>> =
        MutableStateFlow(
            emptyList()
        )
    val contributorsListStream: StateFlow<List<GitCollaborator>> = _contributorsListStream.asStateFlow()

    private var _collaboratorsListStream: MutableStateFlow<List<GitCollaborator>> =
        MutableStateFlow(
            emptyList()
        )
    val collaboratorsListStream: StateFlow<List<GitCollaborator>> = _collaboratorsListStream.asStateFlow()

    init {
        // Get repos from API
        getRepoDetails()
    }

    private fun getRepoDetails() {
        viewModelScope.launch {
            // Get collaborators and contributors

            // contributors
            getCollaboratorsFromRepositoryDetailsUseCase.executeSuspendWithCallback(
                GetCollaboratorsFromRepositoryDetailsUseCaseParams(
                    owner = transitionalDataRepository.getClickedGitRepo().owner.login,
                    name = transitionalDataRepository.getClickedGitRepo().repoName,
                    collaboratorType = CollaboratorType.GET_CONTRIBUTORS
                )
            ) { collaboratorsFromRepoDetailsOutcome ->
                if (collaboratorsFromRepoDetailsOutcome.isSuccessful()) {
                    val collaborators = collaboratorsFromRepoDetailsOutcome.getResult()
                    _contributorsListStream.value = collaborators.collaborators
                } else {
                    val error = collaboratorsFromRepoDetailsOutcome.getError()
                    handleCollaboratorError(error)
                }
            }
        }
    }

    fun getClickedRepo(): GitRepository {
        return transitionalDataRepository.getClickedGitRepo()
    }

    fun toggleFavoriteStatus(gitRepo: GitRepository) {
        gitReposRepository.toggleRepositoryFavoriteStatus(gitRepo)
    }

    fun isFavoriteRepository(gitRepo: GitRepository): Flow<Boolean> {
        return gitReposRepository.isRepositoryFavorited(gitRepo)
    }

    fun onUrlButtonClicked(gitRepo: GitRepository) {
        transitionalDataRepository.setClickedUrl(gitRepo.htmlUrl)
        _navigationStream.value = SecondScreenNavigationEvent.PROJECT_URL
    }

    fun getDestinationUrlString(): String {
        return transitionalDataRepository.getClickedUrl()
    }

    fun isFavoriteCollaborator(gitCollaborator: GitCollaborator): Flow<Boolean> {
        return collaboratorsRepository.isCollaboratorFavorited(gitCollaborator)
    }

    fun toggleCollaboratorFavoriteStatus(gitCollaborator: GitCollaborator) {
        collaboratorsRepository.toggleCollaboratorFavoriteStatus(gitCollaborator)
    }

    private fun handleCollaboratorError(error: ApiException) {
        when(error) {
            ApiException.EmptyResponse,
            is ApiException.GeneralException,
            is ApiException.NetworkException,
            is ApiException.ServerException,
            is ApiException.UnauthorizedException,
            is ApiException.UnexpectedException -> {
                _errorStream.value = SecondScreenErrorState.UnknownErrorHappened
            }
            ApiException.NoInternetException -> _errorStream.value = SecondScreenErrorState.NoInternet
        }.exhaustive
    }

    companion object {
        private const val LOGTAG = "SecondScreenViewModel"
    }
}
