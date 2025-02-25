package com.nsoft.github.domain

/**
 * Generic base class for all FailureReasonExtractors, whose only purpose is to extract
 * the failure reason and cut down boilerplate in UseCases
 *
 * @param ErrorType the exception class this FailureReasonExtractor is expected to extract
 */
abstract class FailureReasonExtractor<ErrorType> {

    /**
     * Method for extracting the error from a combination of response's responseCode and/or errorBody
     *
     * @param responseCode the response code of the response
     * @param errorBody the error body received in the response, if any
     * @return a concrete instance of [ErrorType]
     */
    abstract fun extractError(responseCode: Int, errorBody: String?): ErrorType
}