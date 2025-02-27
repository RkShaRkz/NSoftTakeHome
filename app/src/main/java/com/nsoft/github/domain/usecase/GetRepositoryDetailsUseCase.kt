package com.nsoft.github.domain.usecase

import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.ApiCalls
import com.nsoft.github.data.remote.params.repository_details.GetRepositoryDetailsRequestParams
import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.RepositoryDetails
import com.nsoft.github.domain.usecase.params.GetRepositoryDetailsUseCaseParams
import javax.inject.Inject
import javax.inject.Named

class GetRepositoryDetailsUseCase @Inject constructor(
    @Named(ApiCalls.REPOSITORY_DETAILS)
    apiCall: ApiCall<GetRepositoryDetailsRequestParams, RepositoryDetails>,
): NetworkingUseCase<GetRepositoryDetailsUseCaseParams, GetRepositoryDetailsRequestParams, RepositoryDetails, ApiException>(apiCall) {

    override fun provideParams(params: GetRepositoryDetailsUseCaseParams): GetRepositoryDetailsRequestParams {
        return GetRepositoryDetailsRequestParams(
            owner = params.owner,
            name = params.name
        )
    }

    override fun onSuccess(
        params: GetRepositoryDetailsUseCaseParams,
        result: RepositoryDetails,
        rawJson: String
    ): Outcome<RepositoryDetails, ApiException> {
        //TODO
        return Outcome.successfulOutcome(RepositoryDetails())
    }

    override fun onFailure(
        params: GetRepositoryDetailsUseCaseParams,
        responseCode: Int,
        errorBody: String?
    ): Outcome<RepositoryDetails, ApiException> {
        return Outcome.unsuccessfulOutcome(ApiException.UnexpectedException("responseCode: ${responseCode}, errorBody: ${errorBody}"))
    }

    override fun onError(throwable: Throwable): Outcome<RepositoryDetails, ApiException> {
        return Outcome.unsuccessfulOutcome(ApiException.GeneralException(throwable))
    }
}
