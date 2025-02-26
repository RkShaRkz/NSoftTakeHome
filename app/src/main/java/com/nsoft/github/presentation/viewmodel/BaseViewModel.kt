package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Base [ViewModel] class for all of ${#PROJECT} view models, encapsulating the shared functionality
 * within it, like the common [navigationStream] with it's parametrized [NavigationEventClass]
 * and [errorStream] with it's parametrized [ErrorStateClass]
 *
 * Should be the preferred base class for all ${#PROJECT} presenters
 */
abstract class BaseViewModel<NavigationEventClass, ErrorStateClass>: ViewModel() {

    /**
     * Returns the initial value for [navigationStream]
     */
    abstract fun initialNavigationStreamValue(): NavigationEventClass

    protected val _navigationStream: MutableStateFlow<NavigationEventClass> =
        MutableStateFlow(initialNavigationStreamValue())

    /**
     * The navigation stream, holding information on where the Screen should navigate.
     *
     * **MUST** call [doneNavigating] after navigating to clear the last value
     * @see doneNavigating
     */
    val navigationStream: StateFlow<NavigationEventClass> = _navigationStream

    /**
     * Returns the initial value for [errorStream]
     */
    abstract fun initialErrorStreamValue(): ErrorStateClass

    protected val _errorStream: MutableStateFlow<ErrorStateClass> =
        MutableStateFlow(initialErrorStreamValue())

    /**
     * The error stream, holding information on which error the Screen should show.
     *
     * **MUST** call [errorHandled] after handling the error to clear the last value
     * @see errorHandled
     */
    val errorStream: StateFlow<ErrorStateClass> = _errorStream

    /**
     * Method that **MUST** be called after navigating to clear it's value
     * (or rather, to avoid problems when coming back to it)
     *
     * **NOTE**: Default implementation just returns [initialNavigationStreamValue] here,
     * you might want to override this behaviour
     */
    open fun doneNavigating() {
        _navigationStream.value = initialNavigationStreamValue()
    }

    /**
     * Method that **MUST** be called after handing the error to clear it's value
     * (or rather, to avoid problems when coming back to it)
     */
    fun errorHandled() {
        _errorStream.value = initialErrorStreamValue()
    }
}
