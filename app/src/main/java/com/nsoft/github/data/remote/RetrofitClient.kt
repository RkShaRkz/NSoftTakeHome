package com.nsoft.github.data.remote

import com.nsoft.github.util.MyLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client that will be used for all networking calls
 */
object RetrofitClient {
    public const val API_BASE_URL = "https://api.github.com/"
    private const val TIMEOUT_IN_SECONDS = 30L

    private lateinit var apiRetrofit: Retrofit

    /**
     * Initialization method that initializes the [Retrofit] client and it's backing [OkHttpClient]
     * with a [TIMEOUT_IN_SECONDS] connection and read timeouts.
     *
     * **MUST** be called before attempting to use any networking calls
     */
    fun initializeApiClient(baseUrl: String): Retrofit {
        // If not initialized, or null, initialize again
        if (::apiRetrofit.isInitialized.not() || apiRetrofit == null) {
            // Make sure we use our custom http logger which uses the parametrized LOGTAG
            val logging = HttpLoggingInterceptor(MyLogger.MyOKHttpLogger)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            // Build the HttpClient
            val httpClient = OkHttpClient
                .Builder()
                .addInterceptor(logging)
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)

            apiRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }

        //Otherwise, return the one we already have built
        return apiRetrofit
    }
}
