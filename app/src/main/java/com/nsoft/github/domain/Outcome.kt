package com.nsoft.github.domain

/**
 * Represents the outcome of an operation that can either be successful or failed, carrying information
 * about the exception or error that caused it to fail
 *
 * @param Result The type of the result in case of a successful outcome.
 * @param Error The type of the error in case of a failed outcome.
 */
sealed class Outcome<out Result, out Error> {
    /**
     * Represents a successful outcome.
     *
     * @param result The result of the successful operation.
     */
    data class SuccessfulOutcome<out Result>(internal val result: Result) : Outcome<Result, Nothing>()

    /**
     * Represents a failed outcome.
     *
     * @param error The error of the failed operation.
     */
    data class FailedOutcome<out Error>(internal val error: Error) : Outcome<Nothing, Error>()

    /**
     * Checks whether the outcome is successful.
     *
     * @return `true` if the outcome is successful, `false` otherwise.
     */
    fun isSuccessful(): Boolean = this is SuccessfulOutcome

    /**
     * Retrieves the result of a successful outcome.
     *
     * @return The result of the successful outcome.
     * @throws IllegalStateException if the outcome is not successful.
     */
    fun getResult(): Result {
        if (this is SuccessfulOutcome) {
            return result
        } else {
            throw IllegalStateException("Can only call getResult() on successful outcomes")
        }
    }

    /**
     * Retrieves the error of a failed outcome.
     *
     * @return The error of the failed outcome.
     * @throws IllegalStateException if the outcome is not failed.
     */
    fun getError(): Error {
        if (this is FailedOutcome) {
            return error
        } else {
            throw IllegalStateException("Can only call getError() on failed outcomes")
        }
    }

    companion object {
        /**
         * Creates a successful outcome with the given result.
         *
         * @param result The result of the successful operation.
         * @return A successful outcome.
         */
        fun <Result> successfulOutcome(result: Result): Outcome<Result, Nothing> {
            return SuccessfulOutcome(result)
        }

        /**
         * Creates a failed outcome with the given error.
         *
         * @param error The error of the failed operation.
         * @return A failed outcome.
         */
        fun <Error> unsuccessfulOutcome(error: Error): Outcome<Nothing, Error> {
            return FailedOutcome(error)
        }
    }
}
