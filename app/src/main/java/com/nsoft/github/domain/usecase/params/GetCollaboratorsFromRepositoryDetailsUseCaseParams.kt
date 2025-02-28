package com.nsoft.github.domain.usecase.params

data class GetCollaboratorsFromRepositoryDetailsUseCaseParams(
    val owner: String,
    val name: String
): UseCaseParams()
