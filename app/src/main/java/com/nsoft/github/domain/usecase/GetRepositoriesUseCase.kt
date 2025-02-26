package com.nsoft.github.domain.usecase

import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.ApiCalls
import com.nsoft.github.data.remote.params.get_repositories.GetRepositoriesRequestParams
import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.GitRepositoriesList
import com.nsoft.github.domain.usecase.params.GetRepositoriesUseCaseParams
import com.nsoft.github.util.MyLogger
import javax.inject.Inject
import javax.inject.Named

class GetRepositoriesUseCase @Inject constructor(
@Named(ApiCalls.SEARCH_REPOSITORIES)
    apiCall: ApiCall<GetRepositoriesRequestParams, GitRepositoriesList>,
    //TODO add repos here
): NetworkingUseCase<GetRepositoriesUseCaseParams, GetRepositoriesRequestParams, GitRepositoriesList, ApiException>(apiCall) {

    override fun provideParams(params: GetRepositoriesUseCaseParams): GetRepositoriesRequestParams {
        return GetRepositoriesRequestParams(
            query = "language:kotlin",
            order = "desc",
            sort = "stars",
            perPage = params.perPage,
            page = params.page
        )
    }

    override fun onSuccess(
        params: GetRepositoriesUseCaseParams,
        result: GitRepositoriesList,
        rawJson: String
    ): Outcome<GitRepositoriesList, ApiException> {
        MyLogger.d("SHARK", "onSuccess()\tresult: ${result}")
        return Outcome.successfulOutcome(result)
    }

    override fun onFailure(
        params: GetRepositoriesUseCaseParams,
        responseCode: Int,
        errorBody: String?
    ): Outcome<GitRepositoriesList, ApiException> {
        MyLogger.d("SHARK", "onFailure()\tresponseCode: ${responseCode}, errorBody: ${errorBody}")
        return Outcome.unsuccessfulOutcome(ApiException.UnexpectedException("responseCode: ${responseCode}, errorBody: ${errorBody}"))
    }

    override fun onError(throwable: Throwable): Outcome<GitRepositoriesList, ApiException> {
        return Outcome.unsuccessfulOutcome(ApiException.GeneralException(throwable))
    }
}
