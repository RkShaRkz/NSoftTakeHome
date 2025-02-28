package com.nsoft.github.data.remote.adapters.get_collaborators

import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.calls.CallParams
import com.nsoft.github.data.remote.params.get_collaborators.GetCollaboratorsRequestParams
import javax.inject.Inject

class GetCollaboratorsRequestAdapter @Inject constructor(

): RequestAdapter<GetCollaboratorsRequestParams>() {

    override fun convert(params: GetCollaboratorsRequestParams): Triple<HeadersMap, FieldsMap, QueryMap> {
        val headers = emptyMap<String, String>()
        val fields = emptyMap<String, String>()
        val queries = mapOf<String, String>(
            CallParams.LiteralUrlParams.TARGET_URL_CONSTANT to params.url
        )

        return Triple(headers, fields, queries)
    }
}
