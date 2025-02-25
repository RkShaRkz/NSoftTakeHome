package com.nsoft.github.testreplacements

import com.nsoft.github.data.remote.adapters.FieldsMap
import com.nsoft.github.data.remote.adapters.HeadersMap
import com.nsoft.github.data.remote.adapters.QueryMap
import com.nsoft.github.data.remote.adapters.RequestAdapter
import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.NormalApiCall
import com.nsoft.github.data.remote.params.RequestParams
import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.ResponseDomainData
import com.nsoft.github.domain.usecase.NetworkingUseCase
import com.nsoft.github.domain.usecase.params.UseCaseParams
import com.nsoft.github.testreplacements.NetworkingUseCaseForTesting.Companion.NONNULL_ERROR_BODY
import com.nsoft.github.testreplacements.NetworkingUseCaseForTesting.Companion.TEST_EXCEPTION
import com.nsoft.github.testreplacements.NetworkingUseCaseForTesting.Companion.TEST_VALUE
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

data class TestUseCaseParams(val value: Int): UseCaseParams()
data class TestNetworkParams(val value: Int): RequestParams()
data class TestDomainClass(val value: Int): ResponseDomainData()


class TestRequestAdapter(
    val defaultValue:Triple<HeadersMap, FieldsMap,QueryMap>
): RequestAdapter<TestNetworkParams>() {
    override fun convert(params: TestNetworkParams): Triple<HeadersMap, FieldsMap, QueryMap> {
        return defaultValue
    }
}

class TestResponseAdapter(
    val defaultValue: TestDomainClass
): ResponseAdapter<TestDomainClass>() {
    override fun convert(rawJson: String): TestDomainClass {
        return defaultValue
    }
}



inline fun <reified T> mockGeneric(): T = mock(T::class.java)


/**
 * Creates an annonymous [ApiCall] with [TestNetworkParams] and [TestDomainClass] type parameters.
 *
 * @param call - the lambda to use for the actual call
 * @param requestAdapterConvertedParams - the converted parameters that the request adapter should produce. Defaults to [Pair<HeadersMap, FieldsMap>(emptyMap(), emptyMap())]
 * @param requestAdapter - the request adapter to use. Defaults to [TestRequestAdapter(requestAdapterConvertedParams)]
 * @param responseAdapterConvertedResponse - the converted domain class that the response adapter should produce. Defaults to [TestDomainClass(TEST_VALUE)]
 * @param responseAdapter - the response adapter to use. Defaults to [TestResponseAdapter(responseAdapterConvertedResponse)]
 */
fun createAnnonymousTestApiCall(
    call: (HeadersMap, FieldsMap) -> Call<ResponseBody>,
    requestAdapterConvertedParams: Triple<HeadersMap, FieldsMap,QueryMap> = Triple<HeadersMap, FieldsMap,QueryMap>(emptyMap<String, String>(), emptyMap<String, String>(), emptyMap<String, String>()),
    requestAdapter: RequestAdapter<TestNetworkParams> = TestRequestAdapter(requestAdapterConvertedParams),
    responseAdapterConvertedResponse: TestDomainClass = TestDomainClass(TEST_VALUE),
    responseAdapter: ResponseAdapter<TestDomainClass> = TestResponseAdapter(responseAdapterConvertedResponse)
): ApiCall<TestNetworkParams, TestDomainClass> {
    return NormalApiCall<TestNetworkParams, TestDomainClass>(
        call,
        requestAdapter,
        responseAdapter
    )
}

/**
 * Creates an annonymous mocked OkHttp call
 *
 * **NOTE** while successful calls can have a nullable body, error (failed) calls cannot have a nullable body.
 * In case [responseBodyContent] was null, it will produce a nullable body, which is fine for successful cases,
 * **but for error cases the default value will change** to [NONNULL_ERROR_BODY.toResponseBody(mediaType)]
 *
 * @param mediaType the [MediaType], defaults to "text/plain".
 * @param responseBodyContent the response body to return, defaults to "abc".
 * @param successfulResponse should the response be successful or not. Defaults to [true]. **NOTE** do take a look at [responseCode] param if setting to false
 * @param responseCode the response code to use for the response. Defaults to [200]
 *
 */
fun createAnnonymousMockCall(
    mediaType: MediaType = "text/plain".toMediaType(),
    responseBodyContent: String? = "abc",
    successfulResponse: Boolean = true,
    responseCode: Int = 200,
    stubResponse: Boolean = false
): Call<ResponseBody> {
    val mockCall = mock(Call::class.java) as Call<ResponseBody>
    val responseBody = responseBodyContent?.toResponseBody(mediaType)
    val response = if(successfulResponse) {
        Response.success(responseBody)
    } else {
        Response.error(responseCode, responseBody ?: NONNULL_ERROR_BODY.toResponseBody(mediaType))
    }
    if (stubResponse) {
        `when`(mockCall.execute()).thenReturn(response)
    }

    return mockCall
}

/**
 * Creates an annonymous [NetworkingUseCaseForTesting] with the following parameters
 *
 * @param callToUse - the [ApiCall] to use
 * @param providedNetworkParams - the network params this usecase should provide. Defaults to [TestNetworkParams(TEST_VALUE)]
 * @param successResponse - the response to use for success. Defaults to [Outcome.successfulOutcome(TestDomainClass(TEST_VALUE))]
 * @param failureResponse - the response to use for failures. Defaults to [Outcome.unsuccessfulOutcome(TEST_EXCEPTION)]
 * @param errorResponse - the response to use for errors. Defaults to [Outcome.unsuccessfulOutcome(TEST_EXCEPTION)]
 */
fun createAnnonymousNetworkingUseCaseForTesting(
    callToUse: ApiCall<TestNetworkParams, TestDomainClass>,
    providedNetworkParams: TestNetworkParams = TestNetworkParams(TEST_VALUE),
    successResponse: Outcome<TestDomainClass, Exception> = Outcome.successfulOutcome(TestDomainClass(TEST_VALUE)),
    failureResponse: Outcome<TestDomainClass, Exception> = Outcome.unsuccessfulOutcome(TEST_EXCEPTION),
    errorResponse: Outcome<TestDomainClass, Exception> = Outcome.unsuccessfulOutcome(TEST_EXCEPTION)
): NetworkingUseCaseForTesting {
    return NetworkingUseCaseForTesting(callToUse, providedNetworkParams, successResponse, failureResponse, errorResponse)
}

/**
 * [NetworkingUseCase] but for testing.
 *
 * @param callToUse - the [ApiCall] to use to "fake" the API call. See [createAnnonymousTestApiCall]
 * @param onSuccessResponse - the response to use in the [onSuccess] callback
 * @param onFailureResponse - the response to use in the [onFailure] callback
 * @param onErrorResponse - the resposne to use in the [onError] callback
 */
class NetworkingUseCaseForTesting(
    callToUse: ApiCall<TestNetworkParams, TestDomainClass>,
    val networkParamsToProvide: TestNetworkParams,
    val onSuccessResponse: Outcome<TestDomainClass, Exception>,
    val onFailureResponse: Outcome<TestDomainClass, Exception>,
    val onErrorResponse: Outcome<TestDomainClass, Exception>
): NetworkingUseCase<TestUseCaseParams, TestNetworkParams, TestDomainClass, Exception>(
    callToUse
) {
    /**
     * Expose all methods as public because we want to verify() them
     */


    public override fun provideParams(params: TestUseCaseParams): TestNetworkParams {
        // Well, do nothing
        return networkParamsToProvide
    }

    public override fun onSuccess(
        params: TestUseCaseParams,
        result: TestDomainClass,
        rawJson: String
    ): Outcome<TestDomainClass, Exception> {
        // Do nothing here for now, apart from returning a mock.
        // We will most likely be overriding this via spies in tests
        return onSuccessResponse
    }

    public override fun onFailure(
        params: TestUseCaseParams,
        responseCode: Int,
        errorBody: String?
    ): Outcome<TestDomainClass, Exception> {
        // Same as for onSuccess()

        return onFailureResponse
    }

    public override fun onError(throwable: Throwable): Outcome<TestDomainClass, Exception> {
        // Unfortunatelly, this "if <X> then return <exact same X>" needs to cast the throwable into exception ...
        return when (throwable) {
            is ApiException.NoInternetException -> {
                Outcome.unsuccessfulOutcome(
                    ApiException.NoInternetException
                )
            }

            else -> onErrorResponse
        }
    }


    companion object {
        const val TEST_VALUE: Int = 5
        const val NONNULL_ERROR_BODY = "nonnull error response body"

        val TEST_EXCEPTION: Exception = Exception("This is a test exception")

        val DEFAULT_MOCK_CALLER_INTERFACE: (HeadersMap, FieldsMap) -> Call<ResponseBody> = { headers, fields ->
            // We'll implement the mock behaviour here by returning a mocked Call<ResponseBody> here
            val call = mockGeneric<Call<ResponseBody>>()

            call
        }
    }
}