package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.usecase.GetRepositoriesUseCase
import com.nsoft.github.domain.usecase.params.GetRepositoriesUseCaseParams
import com.nsoft.github.util.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase
): BaseViewModel<FirstScreenNavigationEvent, FirstScreenErrorState>() {

    private var nextPageToFetch = 1

    override fun initialNavigationStreamValue() = FirstScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = FirstScreenErrorState.NoError

    private val _repositoryListStream: MutableLiveData<List<GitRepository>> = MutableLiveData(emptyList())
    val repositoryListStream: LiveData<List<GitRepository>> = _repositoryListStream

    fun getRepositories() {
        viewModelScope.launch {
            getRepositoriesUseCase.executeSuspendWithCallback(
                GetRepositoriesUseCaseParams(
                    page = nextPageToFetch
                )
            ) { result ->
                if (result.isSuccessful()) {
                    val repos = result.getResult().items
                    _repositoryListStream.value = repos
                } else {
                    val error = result.getError()
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
}
