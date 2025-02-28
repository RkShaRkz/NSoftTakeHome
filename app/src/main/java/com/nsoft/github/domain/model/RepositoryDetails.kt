package com.nsoft.github.domain.model

data class RepositoryDetails(
    // Since we have all of the data we need in the other API call, lets just get the collaboratorsUrl here
    val contributorsUrl: String,
    val collaboratorsUrl: String
): ResponseDomainData()
