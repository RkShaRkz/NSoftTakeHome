package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import com.nsoft.github.domain.usecase.GetCollaboratorsFromRepositoryDetailsUseCase
import com.nsoft.github.domain.usecase.params.GetCollaboratorsFromRepositoryDetailsUseCaseParams
import com.nsoft.github.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondScreenViewModel @Inject constructor(
    private val gitReposRepository: GitRepositoriesRepository,
    private val transitionalDataRepository: TransitionalDataRepository,
    private val getCollaboratorsFromRepositoryDetailsUseCase: GetCollaboratorsFromRepositoryDetailsUseCase
): BaseViewModel<SecondScreenNavigationEvent, SecondScreenErrorState>() {

    override fun initialNavigationStreamValue() = SecondScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = SecondScreenErrorState.NoError

    fun getRepoDetails() {
        viewModelScope.launch {
            getCollaboratorsFromRepositoryDetailsUseCase.executeSuspendWithCallback(
                GetCollaboratorsFromRepositoryDetailsUseCaseParams(
                    owner = transitionalDataRepository.getClickedGitRepo().owner.login,
                    name = transitionalDataRepository.getClickedGitRepo().repoName
                )
            ) { collaboratorsFromRepoDetailsOutcome ->
                if (collaboratorsFromRepoDetailsOutcome.isSuccessful()) {
                    val collaborators = collaboratorsFromRepoDetailsOutcome.getResult()
                } else {
                    val error = collaboratorsFromRepoDetailsOutcome.getError()
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

    companion object {
        private const val LOGTAG = "SecondScreenViewModel"
    }
}
