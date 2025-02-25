package com.nsoft.github.domain.usecase

import com.nsoft.github.data.local.NetworkUtils
import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.ApiCallType
import com.nsoft.github.data.remote.calls.CallParams
import com.nsoft.github.data.remote.params.RequestParams
import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.domain.model.ResponseDomainData
import com.nsoft.github.domain.usecase.params.UseCaseParams
import com.nsoft.github.util.exhaustive
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Base class for all [UseCase] instances that rely solely/mostly on networking calls
 *
 *
 * <br><br>
 * So subclassers won't have to composite it with the [HasInternetUseCase] because the base [NetworkingUseCase]
 * already does that, but if you want to put some other condition on performing the networking work,
 * see [LoginWithInternetCheckUseCase.execute] source for more details on how to do that
 *
 * After the network check will still be performed via [hasInternetAvailable]; if there is no internet available,
 * we will **immediatelly** shortcircuit to [onError] wit the [ApiException.NoInternetException] argument.
 * If there is internet, we will prepare networking params via [provideParams], then convert them via [RequestAdapter], and then execute the call
 *
 * For examples on how to handle the NoInternet and wrap it in a contextual ***Exception class, see either
 * [LoginWithInternetCheckUseCase.onError] or [LoginUseCase.onError] sources
 *
 * <br><br>
 * **WARNING:** do **NOT** override [execute] unless you **really** know what you're doing. All of the base
 * [UseCase] class methods call into it, so consider yourself warned
 *
 * <br>
 * @param wrappedCall the wrapped [Retrofit] call [Call] which contains both it's request and response adapters
 * @param Params the concrete parameters for this UseCase, any subclass of [UseCaseParams]
 * @param NetworkParams the concrete parameters for the networking call used by this usecase, part of [wrappedCall] declaration
 * @param DomainClass the actual domain class this usecase obtains/extracts from the networking call, any subclass of [ResponseDomainData]
 * @param ExceptionClass the possible (expected) [Exception]s that can happen during extraction of the [DomainClass]
 *
 * @see hasInternetAvailable
 */
abstract class NetworkingUseCase<
        Params: UseCaseParams,
        NetworkParams: RequestParams,
        DomainClass: ResponseDomainData,
        ExceptionClass: Exception
> constructor(
    private val wrappedCall: ApiCall<NetworkParams, DomainClass>
) : UseCase<Params, Outcome<DomainClass, ExceptionClass>>() {

    /**
     * Method for converting [UseCaseParams] into concrete [RequestParams] for the API network call
     */
    abstract fun provideParams(params: Params): NetworkParams

    /**
     * Check if we have internet connectivity by using [NetworkUtils.isIntenetAvailableUsingAppContext]
     */
    fun hasInternetAvailable(): Boolean {
        return NetworkUtils.isIntenetAvailableUsingAppContext()
    }

    /**
     * The base "execute" method that returns the result of type [UseCaseResult].
     * Use this method for composing with other usecases.
     *
     * Executed on whichever thread the caller is running on, while the networking request itself
     * is submitted to a new, internal single-threaded executor which executes the call (via [wrappedCall]'s [ApiCall.getCall] and executing via [Call.execute] ),
     * returns the future which the usecase is going to wait on in a blocking manner
     *
     * **THIS IS THE METHOD YOU MOST LIKELY WANT TO OVERRIDE/IMPLEMENT**
     */
    override fun execute(params: Params): Outcome<DomainClass, ExceptionClass> {
        // Check for internet, if we don't have internet shortcircuit to handleError(ApiException.NoInternet)
        if (hasInternetAvailable()) {
            // First, prepare the networking params from the usecase params, but we can't do this here now
            // so we will have to let the subclassers do that for us
            val adaptedNetworkingParams = provideParams(params)
            // Convert the params
            val convertedParams = wrappedCall.getRequestAdapter().convert(adaptedNetworkingParams)
            // Make the networking call by scheduling it on an internal Executor
            val executor = Executors.newSingleThreadExecutor()
            val future = executor.submit(Callable<Outcome<DomainClass, ExceptionClass>> {
                try {
                    // Depending on the type of API call, we will create a different kind of CallParams,
                    // which will be CallParams.NormalParams for ApiCallType.NORMAL, or CallParams.QueryParams
                    // for ApiCallType.QUERY
                    //
                    // We need this dumbfuckery due to mixing query-type and normal payload-type APIs sparingly
                    //
                    // For context:
                    // Query APIs target: <base url>/endpoint?queryParam1=<bla>&queryParam2=<bleh> with header/json payload
                    // Normal APIs target: <base url>/endpoint with just the header/json payload

                    // Grab the call (method) to invoke, further modify the convertedParams
                    // based on the type of API call and execute() the call itself
                    val response: Response<ResponseBody> =
                        when (wrappedCall.getApiCallType()) {
                            ApiCallType.QUERY -> {
                                val callParams = CallParams.QueriedParams(
                                    convertedParams.third,
                                    convertedParams.first,
                                    convertedParams.second
                                )

                                wrappedCall.getCall(
                                    callParams
                                ).execute()
                            }

                            ApiCallType.NORMAL -> {
                                val callParams = CallParams.NormalParams(
                                    convertedParams.first,
                                    convertedParams.second
                                )

                                wrappedCall.getCall(
                                    callParams
                                ).execute()
                            }
                        }.exhaustive

                    return@Callable if (response.isSuccessful) {
                        handleSuccess(response, params)
                    } else {
                        handleFailure(response, params)
                    }
                } catch (throwable: Throwable) {
                    return@Callable handleError(throwable)
                }
            })

            // Now block and wait for the future to complete
            return future.get()
        } else {
            // If there is no internet, just return NoInternetException
            return handleError(ApiException.NoInternetException)
        }
    }

    /**
     * Called when we have successfully received a successful response, determined with [Response.isSuccessful]
     */
    private fun handleSuccess(
        response: Response<ResponseBody>,
        params: Params
    ): Outcome<DomainClass, ExceptionClass> {
        val rawJson = response.body()?.string()

        return if (rawJson != null) {
            val result = wrappedCall.getResponseAdapter().convert(rawJson)
            onSuccess(params, result, rawJson)
        } else {
            handleError(ApiException.EmptyResponse)
        }
    }

    /**
     * Called when we have successfully received an unsuccessful response, determined with [Response.isSuccessful]
     */
    private fun handleFailure(
        response: Response<ResponseBody>,
        params: Params
    ): Outcome<DomainClass, ExceptionClass> {
        val errorBody = response.errorBody()?.string()

        return onFailure(params, response.code(), errorBody)
    }

    /**
     * Called when we have been unsucessful to receive a response or some other error happened
     */
    private fun handleError(
        t: Throwable
    ): Outcome<DomainClass, ExceptionClass> {
        return onError(t)
    }

    /**
     * Called when we have successfully received a successful response
     *
     * @param params this [UseCase]'s [UseCaseParams]
     * @param result the [ResponseDomainData] that this UseCase obtained via it's [ApiCall] wrappedCall ([wrappedCall])'s [ResponseAdapter.convert]
     * @param rawJson the raw JSON that was massaged into [result]
     *
     * @return the result the usecase should return to it's caller
     *
     * @see handleSuccess
     */
    protected abstract fun onSuccess(
        params: Params,
        result: DomainClass,
        rawJson: String
    ): Outcome<DomainClass, ExceptionClass>

    /**
     * Called when we have successfully reveived an unsuccessful response
     *
     * @param params this [UseCase]'s [UseCaseParams]
     * @param responseCode the response code of the response
     * @param errorBody the response code of the response, if any
     *
     * @return the result the usecase should return to it's caller
     *
     * @see handleFailure
     */
    protected abstract fun onFailure(
        params: Params,
        responseCode: Int,
        errorBody: String?
    ): Outcome<DomainClass, ExceptionClass>

    /**
     * Called when we have failed to receive any response whatsoever
     *
     * @param throwable the exception that caused the error (in transmission, or whatever other reason)
     *
     * @return the result the usecase should return to it's caller
     *
     * @see handleError
     */
    protected abstract fun onError(
        throwable: Throwable
    ): Outcome<DomainClass, ExceptionClass>
}