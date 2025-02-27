package com.nsoft.github.data.remote.adapters.get_repositories

import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.params.get_repositories.GetRepositoriesRequestParams
import javax.inject.Inject

class GetRepositoriesRequestAdapter @Inject constructor(

): RequestAdapter<GetRepositoriesRequestParams>() {

    override fun convert(params: GetRepositoriesRequestParams): Triple<HeadersMap, FieldsMap, QueryMap> {
        val headers = emptyMap<String, String>()
        val fields = emptyMap<String, String>()
        val queries = mapOf<String, String>(
            QUERY to params.query,
            ORDER to params.order,
            SORT to params.sort,
            PER_PAGE to params.perPage.toString(),  //ughh...
            PAGE to params.page.toString()  // since the map only takes strings, lets try stringifying these ...
        )

        return Triple(headers, fields, queries)
    }

    companion object {
        private const val QUERY = "q"
        private const val ORDER = "order"
        private const val SORT = "sort"
        private const val PER_PAGE = "per_page"
        private const val PAGE = "page"
    }
}
