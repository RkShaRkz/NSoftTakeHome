package com.nsoft.github.domain.repository

import com.nsoft.github.domain.model.GitRepository

/**
 * This is a hack... but I didn't want to use NavArgs for this either, since this is better,
 * even if maybe dirtier. Certainly CLEANer.
 */
interface TransitionalDataRepository {
    fun setClickedGitRepo(gitRepository: GitRepository)
    fun getClickedGitRepo(): GitRepository

    fun setClickedUrl(url: String)
    fun getClickedUrl(): String
}
