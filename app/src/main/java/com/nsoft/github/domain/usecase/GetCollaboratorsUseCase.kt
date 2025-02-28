package com.nsoft.github.domain.usecase

import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.ApiCalls
import com.nsoft.github.data.remote.params.get_collaborators.GetCollaboratorsRequestParams
import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.GitCollaboratorList
import com.nsoft.github.domain.usecase.params.GetCollaboratorsUseCaseParams
import javax.inject.Inject
import javax.inject.Named

class GetCollaboratorsUseCase @Inject constructor(
    @Named(ApiCalls.GET_COLLABORATORS)
    apiCall: ApiCall<GetCollaboratorsRequestParams, GitCollaboratorList>,
): NetworkingUseCase<GetCollaboratorsUseCaseParams, GetCollaboratorsRequestParams, GitCollaboratorList, ApiException>(apiCall) {
    override fun provideParams(params: GetCollaboratorsUseCaseParams): GetCollaboratorsRequestParams {
        return GetCollaboratorsRequestParams(
            url = params.collaboratorsUrl
        )
    }

    override fun onSuccess(
        params: GetCollaboratorsUseCaseParams,
        result: GitCollaboratorList,
        rawJson: String
    ): Outcome<GitCollaboratorList, ApiException> {
        return Outcome.successfulOutcome(result)
    }

    override fun onFailure(
        params: GetCollaboratorsUseCaseParams,
        responseCode: Int,
        errorBody: String?
    ): Outcome<GitCollaboratorList, ApiException> {
        return Outcome.unsuccessfulOutcome(ApiException.UnexpectedException("responseCode: ${responseCode}, errorBody: ${errorBody}"))
    }

    override fun onError(throwable: Throwable): Outcome<GitCollaboratorList, ApiException> {
        return Outcome.unsuccessfulOutcome(ApiException.GeneralException(throwable))
    }
}
