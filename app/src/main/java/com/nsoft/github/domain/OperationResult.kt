package com.nsoft.github.domain

/**
 * Represents the result of an operation that can either be successful or not
 *
 * Similar to Kotlin's [Result], but without the necessity to fail with an exception
 *
 * **NOTE** Whenever you want to use this class, consider using [Outcome] instead
 *
 * @param Type The type of the result in case of a successful outcome.
 */
abstract class OperationResult<out Type> private constructor(
    private val value: Type?,
    private val isSuccessful: Boolean
) {
    /**
     * Represents a successful result.
     *
     * @param result The result of the successful operation.
     */
    private data class SuccessfulResult<out Type>(internal val result: Type) : OperationResult<Type>(result, true)

    /**
     * Checks whether the OperationResult is successful.
     *
     * @return `true` if the OperationResult is successful, `false` otherwise.
     */
    fun isSuccessful(): Boolean = isSuccessful

    /**
     * Retrieves the result of a successful outcome.
     *
     * @return The result of the successful outcome.
     * @throws IllegalStateException if the outcome is not successful.
     */
    fun getResult(): Type {
        if (isSuccessful) {
            return value!!
        } else {
            throw IllegalStateException("Can only call getResult() on successful results")
        }
    }

    companion object {
        /**
         * Creates a successful OperationResult with the given result.
         *
         * @param result The result of the successful operation.
         * @return A successful Result.
         */
        fun <Type> successfulResult(result: Type): OperationResult<Type> {
            return SuccessfulResult<Type>(result)
        }

        /**
         * Creates a failed OperationResult.
         *
         * @return A failed Result.
         */
        fun <Type> unsuccessfulResult(): OperationResult<Type> {
            return object : OperationResult<Type>(null, false) { /* hack */ }
        }
    }
}
