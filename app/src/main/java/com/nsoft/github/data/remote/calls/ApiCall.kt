package com.nsoft.github.data.remote.calls

import com.nsoft.github.data.remote.ApiService
import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.data.remote.params.RequestParams
import com.nsoft.github.domain.model.ResponseDomainData
import com.nsoft.github.domain.usecase.NetworkingUseCase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import retrofit2.http.Path

/**
 * Base class for OUR wrapped version of the form-url-encoded Retrofit call which also contains
 * it's [RequestAdapter] and [ResponseAdapter] bundled with it so that DI can do most of the magic
 *
 * This class should also be used as "wrapper" around normal, payload based API calls that send
 * a JSON to the server
 *
 * @param NetworkParams the concrete subclass of [RequestParams], parameters used for the networking call itself
 * @param DomainClass the actual domain class, subclass of [ResponseDomainData], that this call provides (extracts)
 * from the networking response itself
 *
 * @see ApiCallType
 */
data class NormalApiCall<NetworkParams : RequestParams, out DomainClass : ResponseDomainData>
@Inject constructor(
    private val retrofitCall: (HeadersMap, FieldsMap) -> Call<ResponseBody>,
    private val requestAdapter: RequestAdapter<NetworkParams>,
    private val responseAdapter: ResponseAdapter<DomainClass>
): ApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.NORMAL) {

    override fun getCall(params: CallParams): Call<ResponseBody> {
        require(params is CallParams.NormalParams)
        return retrofitCall(params.headers, params.fields)
    }
}

/**
 * Base class for OUR wrapped version of the QUERY Retrofit call which also contains it's
 * [RequestAdapter] and [ResponseAdapter] bundled with it so that DI can do most of the magic.
 *
 * This class should be used as a "wrapper" around query-based API calls such as
 * ```
 * https://backend.com/endpoint?{queryParam1=queryParam1Value}&{queryParam2=queryParam2Value}..
 * ```
 *
 * @param NetworkParams the concrete subclass of [RequestParams], parameters used for the networking call itself
 * @param DomainClass the actual domain class, subclass of [ResponseDomainData], that this call provides (extracts)
 * from the networking response itself
 *
 * @see ApiCallType
 */
data class QueriedApiCall<NetworkParams : RequestParams, out DomainClass : ResponseDomainData>
@Inject constructor(
    private val retrofitCall: (QueryMap, HeadersMap) -> Call<ResponseBody>,
    private val requestAdapter: RequestAdapter<NetworkParams>,
    private val responseAdapter: ResponseAdapter<DomainClass>
): ApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.QUERY) {

    override fun getCall(params: CallParams): Call<ResponseBody> {
        require(params is CallParams.QueriedParams)
        return retrofitCall(params.query, params.headers)
    }
}

/**
 * Base class for OUR wrapped version of the PATH Retrofit call which also contains it's
 * [RequestAdapter] and [ResponseAdapter] bundled with it so that DI can do most of the magic.
 *
 * This class should be used as a "wrapper" around path-based API calls such as
 * ```
 * https://backend.com/endpoint/{pathParam1}/{pathParam2}/...
 * ```
 *
 * **IMPORTANT NOTE:** to comply with the rest of the [ApiCall] definition, this call will take it's
 * path (see [Path]) parameters from the **third** map ([QueryMap]) returned by the [RequestAdapter.convert],
 * but to preserve ordering, the RequestAdapter should put them in the map with [PathApiCallConstants] keys.
 *
 * For more details also see [NetworkingUseCase] portion that references [ApiCallType.PATH]
 *
 *
 * @param NetworkParams the concrete subclass of [RequestParams], parameters used for the networking call itself
 * @param DomainClass the actual domain class, subclass of [ResponseDomainData], that this call provides (extracts)
 * from the networking response itself
 *
 * @see ApiCallType
 */

sealed class PathApiCall<NetworkParams : RequestParams, out DomainClass : ResponseDomainData>(
    requestAdapter: RequestAdapter<NetworkParams>,
    responseAdapter: ResponseAdapter<DomainClass>,
    apiCallType: ApiCallType
) : ApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, apiCallType) {

    /**
     * One 'path' element version of [PathApiCall] kind of [ApiCall].
     * Will serve endpoints looking like this:
     * ```
     * https://www.backend.com/path_element1
     * ```
     *
     * @see PathApiCall
     */
    data class OnePathElementsApiCall<PathType, NetworkParams : RequestParams, out DomainClass : ResponseDomainData> @Inject constructor(
        private val retrofitCall: (PathType) -> Call<ResponseBody>,
        private val requestAdapter: RequestAdapter<NetworkParams>,
        private val responseAdapter: ResponseAdapter<DomainClass>
    ) : PathApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.PATH) {

        override fun getCall(params: CallParams): Call<ResponseBody> {
            require(params is CallParams.PathParams)
            return retrofitCall(params.pathParams[0] as PathType)
        }
    }

    /**
     * Two 'path' elements version of [PathApiCall] kind of [ApiCall].
     * Will serve endpoints looking like this:
     * ```
     * https://www.backend.com/path_element1/path_element2
     * ```
     *
     * @see PathApiCall
     */
    data class TwoPathElementsApiCall<PathType1, PathType2, NetworkParams : RequestParams, out DomainClass : ResponseDomainData> @Inject constructor(
        private val retrofitCall: (PathType1, PathType2) -> Call<ResponseBody>,
        private val requestAdapter: RequestAdapter<NetworkParams>,
        private val responseAdapter: ResponseAdapter<DomainClass>
    ) : PathApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.PATH) {

        override fun getCall(params: CallParams): Call<ResponseBody> {
            require(params is CallParams.PathParams)
            return retrofitCall(
                params.pathParams[0] as PathType1,
                params.pathParams[1] as PathType2
            )
        }
    }

    /**
     * Three 'path' elements version of [PathApiCall] kind of [ApiCall].
     * Will serve endpoints looking like this:
     * ```
     * https://www.backend.com/path_element1/path_element2/path_element3
     * ```
     *
     * @see PathApiCall
     */
    data class ThreePathElementsApiCall<PathType1, PathType2, PathType3, NetworkParams : RequestParams, out DomainClass : ResponseDomainData> @Inject constructor(
        private val retrofitCall: (PathType1, PathType2, PathType3) -> Call<ResponseBody>,
        private val requestAdapter: RequestAdapter<NetworkParams>,
        private val responseAdapter: ResponseAdapter<DomainClass>
    ) : PathApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.PATH) {

        override fun getCall(params: CallParams): Call<ResponseBody> {
            require(params is CallParams.PathParams)
            return retrofitCall(
                params.pathParams[0] as PathType1,
                params.pathParams[1] as PathType2,
                params.pathParams[2] as PathType3
            )
        }
    }
}

/**
 * Base class for OUR wrapped version of the LITERAL_URL Retrofit call which also contains it's
 * [RequestAdapter] and [ResponseAdapter] bundled with it so that DI can do most of the magic.
 *
 * This class should be used as a "wrapper" around query-based API calls such as
 * ```
 * <literal url>
 * ```
 *
 * @param NetworkParams the concrete subclass of [RequestParams], parameters used for the networking call itself
 * @param DomainClass the actual domain class, subclass of [ResponseDomainData], that this call provides (extracts)
 * from the networking response itself
 *
 * @see ApiCallType
 */
data class LiteralUrlApiCall<NetworkParams : RequestParams, out DomainClass : ResponseDomainData>
@Inject constructor(
    private val retrofitCall: (String) -> Call<ResponseBody>,
    private val requestAdapter: RequestAdapter<NetworkParams>,
    private val responseAdapter: ResponseAdapter<DomainClass>
): ApiCall<NetworkParams, DomainClass>(requestAdapter, responseAdapter, ApiCallType.LITERAL_URL) {

    override fun getCall(params: CallParams): Call<ResponseBody> {
        require(params is CallParams.LiteralUrlParams)
        return retrofitCall(params.url)
    }
}


/**
 * Base class for OUR wrapped version of the Retrofit call can *either* be a normal call or a queried call
 * which also contains its [RequestAdapter] and [ResponseAdapter] bundled with it so that DI can do most of the magic
 *
 * @param NetworkParams the concrete subclass of [RequestParams], parameters used for the networking call itself
 * @param DomainClass the actual domain class, subclass of [ResponseDomainData], that this call provides (extracts)
 * from the networking response itself
 *
 * @see NormalApiCall
 * @see QueriedApiCall
 */
sealed class ApiCall<NetworkParams : RequestParams, out DomainClass : ResponseDomainData>(
    private val requestAdapter: RequestAdapter<NetworkParams>,
    private val responseAdapter: ResponseAdapter<DomainClass>,
    private val apiCallType: ApiCallType
) {
    /**
     * Get this call's [RequestAdapter]
     */
    fun getRequestAdapter(): RequestAdapter<NetworkParams> {
        return requestAdapter
    }

    /**
     * Get this call's [ResponseAdapter]
     */
    fun getResponseAdapter(): ResponseAdapter<DomainClass> {
        return responseAdapter
    }

    /**
     * Get the API call type.
     */
    fun getApiCallType(): ApiCallType = apiCallType

    /**
     * Get this "call", or rather it's underlying [Retrofit] call method invoked by [AuthService] or [ApiService]
     */
    abstract fun getCall(params: CallParams): Call<ResponseBody>
}

/**
 * Enum class denoting the API call type - whether it's a "Query" API (an API call that also needs
 * to put in parameters as query parameters in the endpoint itself besides the header/values payload)
 * or a "Normal" API that just uses the header/values payload.
 *
 * A "Path" api is one that looks like https://www.backend.com/endpoint/path1/path2/...
 *
 * A "Literal URL" api is one that looks like any random URL obtained from anywhere
 */
enum class ApiCallType { QUERY, NORMAL, PATH, LITERAL_URL }

/**
 * Sealed class for encapsulating parameters required for different API call types.
 *
 * All of these parameters are created by [NetworkingUseCase] exclusively, in it's [NetworkingUseCase.provideParams]
 * method
 *
 * @see NormalParams
 * @see QueriedParams
 * @see PathParams
 * @see LiteralUrlParams
 */
sealed class CallParams {
    /**
     * "Normal" params for "normal" API calls which just attach [headers] and have values ([fields])
     */
    data class NormalParams(val headers: HeadersMap, val fields: FieldsMap) : CallParams()

    /**
     * "Queried" params for "query" API calls which attach [headers] and have values ([fields])
     * alongside their "query parameters" ([query]) which get appended to the endpoint itself
     */
    data class QueriedParams(val query: QueryMap, val headers: HeadersMap, val fields: FieldsMap) : CallParams()

    /**
     * "Path" params for "path" API calls which attach the elements of [pathParams] list into the endpoint itself
     * These are created from the [QueryMap] part of the converted parameters once the [RequestAdapter] is
     * done with them
     */
    data class PathParams(val pathParams: List<Any>) : CallParams()

    /**
     * "Literal URL" params for the 'literal url' API calls which really just target any literal URL as it's endpoint
     * These are created from the *only* value (`url`) in the [QueryMap] after the [RequestAdapter] converts it
     */
    data class LiteralUrlParams(val url: String): CallParams() {
        companion object {
            public const val TARGET_URL_CONSTANT = "url"
        }
    }
}

class PathApiCallConstants {
    companion object {
        public const val PATH_PARAM1_KEY = "pathParam1"
        public const val PATH_PARAM2_KEY = "pathParam2"
        public const val PATH_PARAM3_KEY = "pathParam3"
        public const val PATH_PARAM4_KEY = "pathParam4"
        public const val PATH_PARAM5_KEY = "pathParam5"
        public const val PATH_PARAM6_KEY = "pathParam6"
        public const val PATH_PARAM7_KEY = "pathParam7"
        public const val PATH_PARAM8_KEY = "pathParam8"
        public const val PATH_PARAM9_KEY = "pathParam9"
        public const val PATH_PARAM10_KEY = "pathParam10"
    }
}
