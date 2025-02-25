package com.nsoft.github.domain.exception

import com.nsoft.github.domain.usecase.NetworkingUseCase

/**
 * General networking/API errors that can happen, mostly covering connection/transmission problems
 * which have nothing to do with the API (and it's errors) itself
 */
sealed class ApiException(message: String) : Exception(message) {
    /**
     * Returned automatically by the [NetworkingUseCase] when there is no internet
     */
    object NoInternetException : ApiException("NoInternet") {
        private fun readResolve(): Any = NoInternetException
    }

    /**
     * So far, only "automatically" returned by [NetworkingUseCase] when the response is [null]
     */
    object EmptyResponse: ApiException("EmptyResponse") {
        private fun readResolve(): Any = EmptyResponse
    }

    class NetworkException(message: String) : ApiException(message)
    class ServerException(message: String, val code: Int) : ApiException(message)
    class UnauthorizedException(message: String) : ApiException(message)
    class UnexpectedException(message: String) : ApiException(message)
    class GeneralException(wrappedException: Throwable) : ApiException(wrappedException.toString())
}
