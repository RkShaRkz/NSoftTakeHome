package com.nsoft.github.data.remote.adapters

import com.google.gson.Gson
import com.nsoft.github.domain.model.GitRepositoriesList
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.model.GitRepositoryOwner
import com.nsoft.github.domain.model.gsonmodels.GitHubSearchResponse
import org.threeten.bp.Instant
import javax.inject.Inject

class GetRepositoriesResponseAdapter @Inject constructor(
    val gson: Gson
): ResponseAdapter<GitRepositoriesList>() {
    override fun convert(rawJson: String): GitRepositoriesList {
        val response = gson.fromJson(rawJson, GitHubSearchResponse::class.java)
        val repoList = mutableListOf<GitRepository>()
        // Remap and massage as necessary
        for (repo in response.items) {
            repoList.add(
                GitRepository(
                    owner = GitRepositoryOwner(
                        login = repo.owner.login,
                        avatarUrl = repo.owner.avatarUrl,
                        htmlUrl = repo.owner.htmlUrl
                    ),
                    repoName = repo.fullName,
                    description = repo.description,
                    language = repo.language,
                    stargazersCount = repo.stargazersCount,
                    forksCount = repo.forksCount,
                    openIssues = repo.openIssues,
                    watchersCount = repo.watchersCount,
                    defaultBranch = repo.defaultBranch,
                    createdAt = Instant.parse(repo.createdAt),
                    updatedAt = Instant.parse(repo.updatedAt),
                    collaboratorsUrl = repo.collaboratorsUrl,
                    contributorsUrl = repo.contributorsUrl,
                    htmlUrl = repo.htmlUrl
                )
            )
        }

        return GitRepositoriesList(repoList.toList())
    }



}
