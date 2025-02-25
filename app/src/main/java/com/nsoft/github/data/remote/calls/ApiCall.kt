package com.nsoft.github.data.remote.calls

import com.nsoft.github.data.remote.ApiService
import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.data.remote.params.RequestParams
import com.nsoft.github.domain.model.ResponseDomainData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject

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
 * https://backend.com/endpoint/{queryParam}/...
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
 * or a "Normal" API that just uses the header/values payload
 */
enum class ApiCallType { QUERY, NORMAL }

/**
 * Sealed class for encapsulating parameters required for different API call types.
 *
 * @see NormalParams
 * @see QueriedParams
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
}