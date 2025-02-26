package com.nsoft.github.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap

/**
 * Service used to target the actual API server that provides API calls with their endpoints
 *
 * @see RetrofitClient.API_BASE_URL
 */
interface ApiService {
//    https://api.github.com/search/repositories?q=language:kotlin&order=desc&sort=stars&per_page=20&page=1
    @GET("/search/repositories")
    fun getRepositories(@QueryMap queries: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>
}
