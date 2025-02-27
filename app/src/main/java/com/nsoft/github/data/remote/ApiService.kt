package com.nsoft.github.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Service used to target the actual API server that provides API calls with their endpoints
 *
 * @see RetrofitClient.API_BASE_URL
 */
interface ApiService {
    @GET("/search/repositories")
    fun getRepositories(@QueryMap queries: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @GET("/repos/{owner}/{name}")
    fun getRepositoryDetails(@Path("owner") owner: String,@Path("name") name: String): Call<ResponseBody>
//    @GET("/repos/")
//    fun getRepositoryDetails(@QueryMap queries: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>
}
