package com.nsoft.github.data.repository

import android.net.Uri
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import javax.inject.Inject

class TransitionalDataRepositoryImpl @Inject constructor(
    // Since we're not even gonna use sharedpref for this, lets just keep the values in memory
): TransitionalDataRepository {
    private lateinit var clickedGitRepo: GitRepository
    private lateinit var clickedUrl: String

    override fun setClickedGitRepo(gitRepository: GitRepository) {
        clickedGitRepo = gitRepository
    }

    override fun getClickedGitRepo(): GitRepository {
        return clickedGitRepo
    }

    override fun setClickedUrl(url: String) {
//        clickedUrl = Uri.parse(url)
        clickedUrl = url
    }

    override fun getClickedUrl(): String {
        return clickedUrl
    }
}
