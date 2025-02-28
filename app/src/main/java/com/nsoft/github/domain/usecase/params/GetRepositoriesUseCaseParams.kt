package com.nsoft.github.domain.usecase.params

data class GetRepositoriesUseCaseParams(
    val perPage: Int = 20,
    val page: Int
): UseCaseParams()
