package com.nsoft.github.domain.model

/**
 * Domain class containing a list of git repos
 */
data class GitRepositoriesList(
    val items: List<GitRepository>
): ResponseDomainData()
