package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import com.nsoft.github.domain.usecase.GetCollaboratorsUseCase
import com.nsoft.github.domain.usecase.GetRepositoryDetailsUseCase
import com.nsoft.github.domain.usecase.params.GetCollaboratorsUseCaseParams
import com.nsoft.github.domain.usecase.params.GetRepositoryDetailsUseCaseParams
import com.nsoft.github.util.MyLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondScreenViewModel @Inject constructor(
    // We don't really even need this usecase, because the first API call returned all the information
    // we need, but lets just use this one to populate collaborators ...
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val transitionalDataRepository: TransitionalDataRepository,
    private val gitReposRepository: GitRepositoriesRepository,
    private val getCollaboratorsUseCase: GetCollaboratorsUseCase
): BaseViewModel<SecondScreenNavigationEvent, SecondScreenErrorState>() {

    override fun initialNavigationStreamValue() = SecondScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = SecondScreenErrorState.NoError

    fun getRepoDetails() {
        viewModelScope.launch {
            // TODO replace with new getCollaboratorsFromRepositoryDetailsUseCase
            getRepositoryDetailsUseCase.executeSuspendWithCallback(
                GetRepositoryDetailsUseCaseParams(
                    owner = transitionalDataRepository.getClickedGitRepo().owner.login,
                    name = transitionalDataRepository.getClickedGitRepo().repoName
                ),
            ) { repoDetailsOutcome ->
                if (repoDetailsOutcome.isSuccessful()) {
                    val repoDetails = repoDetailsOutcome.getResult()
                    MyLogger.e(LOGTAG, "repo details: ${repoDetails}")
                } else {
                    val error = repoDetailsOutcome.getError()
                    MyLogger.e(LOGTAG, "repoDetailsOutcome error: ${error}")
                }
            }

            // And get Collaborators just to see if it works ...
            getCollaboratorsUseCase.executeSuspendWithCallback(
                GetCollaboratorsUseCaseParams(transitionalDataRepository.getClickedGitRepo().contributorsUrl)
            ) { collaboratorsOutcome ->
                if (collaboratorsOutcome.isSuccessful()) {
                    val collaborators = collaboratorsOutcome.getResult()
                    MyLogger.e(LOGTAG, "collaborators: ${collaborators}")
                } else {
                    val error = collaboratorsOutcome.getError()
                    MyLogger.e(LOGTAG, "collaboratorsOutcome error: ${error}")
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
