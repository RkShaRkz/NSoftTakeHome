package com.nsoft.github.domain.usecase.params

data class GetRepositoryDetailsUseCaseParams(
    val owner: String,
    val name: String
): UseCaseParams()
