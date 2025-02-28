package com.nsoft.github.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url

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

    // This one is a fully dynamic URL coming from the API, so we will just use an empty @GET for it
    @GET
    fun getCollaboratorsFromRepository(@Url url: String): Call<ResponseBody>
}
