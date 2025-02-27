package com.nsoft.github.data.remote.adapters.repository_details

import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.domain.model.RepositoryDetails
import com.nsoft.github.util.MyLogger
import javax.inject.Inject

class GetRepositoryDetailsResponseAdapter @Inject constructor(

): ResponseAdapter<RepositoryDetails>() {

    override fun convert(rawJson: String): RepositoryDetails {
        MyLogger.e("GetRepositoryDetailsResponseAdapter", "rawJson = ${rawJson}")
        //TODO
        return RepositoryDetails()
    }
}
