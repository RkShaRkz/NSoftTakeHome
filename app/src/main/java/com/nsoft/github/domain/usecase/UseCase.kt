package com.nsoft.github.domain.usecase

import com.nsoft.github.domain.usecase.params.UseCaseParams

/**
 * Base class for all UseCases, parametrized by the [UseCaseParams] and [UseCaseResult]
 * classes.
 *
 * Callers of this class (and it's subclass) need only call [execute]
 */
abstract class UseCase<in Params : UseCaseParams, out UseCaseResult> {
    /**
     * The base "execute" method that returns the result of type [UseCaseResult].
     * Use this method for composing with other usecases.
     *
     * Executed on whichever thread the caller is running on.
     *
     * **THIS IS THE METHOD YOU MOST LIKELY WANT TO OVERRIDE/IMPLEMENT**
     */
    abstract fun execute(params: Params): UseCaseResult

    /**
     * Similar to [execute], but with a lambda "callback" parameter which simply calls [execute]
     * and then calls the [callback] with the result
     *
     * Executed on whichever thread the caller is running on.
     *
     * **WARNING:** Be sure **you know what you're doing** if you're overriding this
     */
    fun executeWithCallback(params: Params, callback: (UseCaseResult) -> Unit) {
        val result = execute(params)
        callback(result)
    }

    /**
     * A suspend version of [execute] which simply returns the result of [execute]
     *
     * @see execute(Params)
     */
    suspend fun executeSuspend(params: Params): UseCaseResult {
        return execute(params)
    }

    /**
     * A suspend version of [execute] (with callback) which simply calls [execute] and
     * calls the [callback] with the result
     *
     * @see executeWithCallback
     */
    suspend fun executeSuspendWithCallback(params: Params, callback: (UseCaseResult) -> Unit) {
        val result = executeSuspend(params)
        callback(result)
    }

}