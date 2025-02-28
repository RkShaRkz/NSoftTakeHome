package com.nsoft.github.data.remote.params.repository_details

import com.nsoft.github.data.remote.params.RequestParams

data class GetRepositoryDetailsRequestParams(
    val owner: String,
    val name: String
): RequestParams()
