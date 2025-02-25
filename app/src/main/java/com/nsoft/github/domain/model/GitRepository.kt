package com.nsoft.github.domain.model

import org.threeten.bp.Instant

/**
 * A class that models a git repository, containing just what we need to show
 */
data class GitRepository(
    val owner: GitRepositoryOwner,
    val repoName: String,
    val description: String,
    val language: String,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssues: Int,
    val watchersCount: Int,
    // Additional fields for SecondScreen here
    val defaultBranch: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val collaboratorsUrl: String,   // "suradnik" can really be both of these so fuck it
    val contributorsUrl: String,
    val htmlUrl: String
): ResponseDomainData()

data class GitRepositoryOwner(
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)
