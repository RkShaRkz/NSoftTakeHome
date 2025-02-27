package com.nsoft.github.domain.usecase.params

import com.nsoft.github.domain.usecase.GetCollaboratorsUseCase

/**
 * [UseCaseParams] for the [GetCollaboratorsUseCase]
 */
data class GetCollaboratorsUseCaseParams(
    val collaboratorsUrl: String
): UseCaseParams()
