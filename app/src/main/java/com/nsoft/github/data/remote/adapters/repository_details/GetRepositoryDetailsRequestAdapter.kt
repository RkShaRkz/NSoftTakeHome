package com.nsoft.github.data.remote.adapters.repository_details

import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.params.repository_details.GetRepositoryDetailsRequestParams
import javax.inject.Inject

class GetRepositoryDetailsRequestAdapter @Inject constructor(

): RequestAdapter<GetRepositoryDetailsRequestParams>() {

    override fun convert(params: GetRepositoryDetailsRequestParams): Triple<HeadersMap, FieldsMap, QueryMap> {
        // While this one doesn't technically have any parameters, lets try something ...
        val headers = emptyMap<String, String>()
        val fields = emptyMap<String, String>()
        val queries = mapOf<String, String>(
            OWNER to params.owner,
            NAME to params.name
        )

        return Triple(headers, fields, queries)
    }

    companion object {
        private const val OWNER = "owner"
        private const val NAME = "name"
    }
}
