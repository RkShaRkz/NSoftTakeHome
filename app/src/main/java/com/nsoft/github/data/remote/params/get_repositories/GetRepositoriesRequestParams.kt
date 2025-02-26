package com.nsoft.github.data.remote.params.get_repositories

import com.nsoft.github.data.remote.params.RequestParams

/**
 * Model class for the "get_repositories" RequestParams
 */
data class GetRepositoriesRequestParams(
    val query: String,
    val order: String,
    val sort: String,
    val perPage: Int,
    val page: Int
): RequestParams()
