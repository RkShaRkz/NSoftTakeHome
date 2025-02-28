package com.nsoft.github.domain.usecase

import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.GitCollaboratorList
import com.nsoft.github.domain.usecase.params.GetCollaboratorsFromRepositoryDetailsUseCaseParams
import com.nsoft.github.domain.usecase.params.GetCollaboratorsUseCaseParams
import com.nsoft.github.domain.usecase.params.GetRepositoryDetailsUseCaseParams
import javax.inject.Inject

class GetCollaboratorsFromRepositoryDetailsUseCase @Inject constructor(
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val getCollaboratorsUseCase: GetCollaboratorsUseCase
): UseCase<GetCollaboratorsFromRepositoryDetailsUseCaseParams, Outcome<GitCollaboratorList, ApiException>>() {
    override fun execute(params: GetCollaboratorsFromRepositoryDetailsUseCaseParams): Outcome<GitCollaboratorList, ApiException> {
        // Ok, lets run the getRepoDetails first, and when/if it succeeds, we run the getCollaborators one and return that.
        // otherwise, we return the error that happened.
        val getRepoDetailsOutcome = getRepositoryDetailsUseCase.execute(
            GetRepositoryDetailsUseCaseParams(
                owner = params.owner,
                name = params.name
            )
        )
        if (getRepoDetailsOutcome.isSuccessful()) {
            val getRepoDetailsResult = getRepoDetailsOutcome.getResult()
            val getCollaboratorsOutcome = getCollaboratorsUseCase.execute(
                GetCollaboratorsUseCaseParams(
//                    collaboratorsUrl = getRepoDetailsResult.collaboratorsUrl
                    collaboratorsUrl = getRepoDetailsResult.contributorsUrl
                )
            )

            if (getCollaboratorsOutcome.isSuccessful()) {
                val collaborators = getCollaboratorsOutcome.getResult()
                return Outcome.successfulOutcome(collaborators)
            } else {
                val getCollaboratorsError = getCollaboratorsOutcome.getError()
                return Outcome.unsuccessfulOutcome(getCollaboratorsError)
            }
        } else {
            val getRepoDetailsError = getRepoDetailsOutcome.getError()
            return Outcome.unsuccessfulOutcome(getRepoDetailsError)
        }
    }
}
