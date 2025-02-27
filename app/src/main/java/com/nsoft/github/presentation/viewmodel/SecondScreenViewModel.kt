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
import com.nsoft.github.domain.usecase.GetRepositoryDetailsUseCase
import com.nsoft.github.domain.usecase.params.GetRepositoryDetailsUseCaseParams
import com.nsoft.github.util.MyLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondScreenViewModel @Inject constructor(
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val transitionalDataRepository: TransitionalDataRepository,
    private val gitReposRepository: GitRepositoriesRepository
): BaseViewModel<SecondScreenNavigationEvent, SecondScreenErrorState>() {

    override fun initialNavigationStreamValue() = SecondScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = SecondScreenErrorState.NoError

    fun getRepoDetails() {
        viewModelScope.launch {
            getRepositoryDetailsUseCase.executeSuspendWithCallback(
                GetRepositoryDetailsUseCaseParams(
                    owner = transitionalDataRepository.getClickedGitRepo().owner.login,
                    name = transitionalDataRepository.getClickedGitRepo().repoName
                ),
            ) { repoDetailsOutcome ->
                if (repoDetailsOutcome.isSuccessful()) {
                    val repoDetails = repoDetailsOutcome.getResult()
                    MyLogger.e("SecondScreenViewModel", "repo details: ${repoDetails}")
                } else {
                    val error = repoDetailsOutcome.getError()
                    MyLogger.e("SecondScreenViewModel", "error: ${error}")
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
}
