package com.nsoft.github.domain.usecase.params

data class GetCollaboratorsFromRepositoryDetailsUseCaseParams(
    val owner: String,
    val name: String,
    val collaboratorType: CollaboratorType = CollaboratorType.GET_CONTRIBUTORS
): UseCaseParams()

enum class CollaboratorType { GET_COLLABORATORS, GET_CONTRIBUTORS }
