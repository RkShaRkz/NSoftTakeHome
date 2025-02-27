package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.usecase.GetRepositoriesUseCase
import com.nsoft.github.domain.usecase.params.GetRepositoriesUseCaseParams
import com.nsoft.github.util.MyLogger
import com.nsoft.github.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val gitReposRepository: GitRepositoriesRepository
): BaseViewModel<FirstScreenNavigationEvent, FirstScreenErrorState>() {

    private var nextPageToFetch = 1
    private var filterCriteria = ""

    override fun initialNavigationStreamValue() = FirstScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = FirstScreenErrorState.NoError

    var _repositoryListStream: MutableStateFlow<List<GitRepository>> =
        MutableStateFlow<List<GitRepository>>(
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

    fun getRepositories() {
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

    fun isFavoriteFlow(gitRepo: GitRepository): Flow<Boolean> {
        return gitReposRepository.isRepositoryFavorited(gitRepo)
    }

    fun onItemClicked(gitRepo: GitRepository) {

    }

    fun setFilterCriteria(filterString: String) {
        filterCriteria = filterString
        viewModelScope.launch {
            refreshInnerStream()
        }
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
