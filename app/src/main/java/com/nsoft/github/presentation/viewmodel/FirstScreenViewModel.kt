package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import com.nsoft.github.domain.usecase.GetRepositoriesUseCase
import com.nsoft.github.domain.usecase.params.GetRepositoriesUseCaseParams
import com.nsoft.github.util.MyLogger
import com.nsoft.github.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val gitReposRepository: GitRepositoriesRepository,
    private val transitionalDataRepository: TransitionalDataRepository
): BaseViewModel<FirstScreenNavigationEvent, FirstScreenErrorState>() {

    private var nextPageToFetch = 1
    private var filterCriteria = ""

    override fun initialNavigationStreamValue() = FirstScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = FirstScreenErrorState.NoError

    private var _repositoryListStream: MutableStateFlow<List<GitRepository>> =
        MutableStateFlow(
            emptyList()
        )
    val repositoryListStream: StateFlow<List<GitRepository>> = _repositoryListStream.asStateFlow()

    init {
        // Get repos from API
        getRepositories()
        // Observe Room's StateFlow and update the inner MutableStateFlow
        viewModelScope.launch {
            gitReposRepository.getAllRepositoriesFiltered(filterCriteria)
                .collect { newList ->
                    _repositoryListStream.value = newList
                }
        }
    }

    private fun getRepositories() {
        viewModelScope.launch {
            getRepositoriesUseCase.executeSuspendWithCallback(
                GetRepositoriesUseCaseParams(
                    page = nextPageToFetch
                )
            ) { result ->
                if (result.isSuccessful()) {
                    val repos = result.getResult().items
                    // We don't really care about this, the UseCase will write to the repository
                    // which will publish new items which will then be observed by the UI through our StateFlow
                } else {
                    val error = result.getError()
                    MyLogger.e(LOGTAG, "Error happened! error: ${error}")
                    when (error) {
                        ApiException.EmptyResponse,
                        is ApiException.GeneralException,
                        is ApiException.NetworkException,
                        is ApiException.ServerException,
                        is ApiException.UnauthorizedException,
                        is ApiException.UnexpectedException -> {
                            _errorStream.value = FirstScreenErrorState.UnknownErrorHappened
                        }
                        ApiException.NoInternetException -> {
                            _errorStream.value = FirstScreenErrorState.NoInternet
                        }
                    }.exhaustive
                }
            }
        }
    }

    fun fetchNextPage() {
        nextPageToFetch++
        getRepositories()
    }

    fun toggleFavoriteStatus(gitRepo: GitRepository) {
        gitReposRepository.toggleRepositoryFavoriteStatus(gitRepo)
    }

    fun isFavoriteRepositoryFlow(gitRepo: GitRepository): Flow<Boolean> {
        return gitReposRepository.isRepositoryFavorited(gitRepo)
    }

    fun onItemClicked(gitRepo: GitRepository) {
        transitionalDataRepository.setClickedGitRepo(gitRepo)
        _navigationStream.value = FirstScreenNavigationEvent.SECOND_SCREEN
    }

    // We'll need this one when we come back to the first screen so return the current filter criteria
    fun getFilterCriteria(): String {
        return filterCriteria
    }

    fun setFilterCriteria(filterString: String) {
        filterCriteria = filterString
        viewModelScope.launch {
            refreshInnerStream()
        }
    }

    fun onGoToThirdScreenClicked() {
        _navigationStream.value = FirstScreenNavigationEvent.THIRD_SCREEN
    }

    private suspend fun refreshInnerStream() {
        gitReposRepository.getAllRepositoriesFiltered(filterCriteria)
            .collect { newList ->
                _repositoryListStream.value = newList
            }
    }

    companion object {
        private const val LOGTAG = "FirstScreenViewModel"
    }
}
