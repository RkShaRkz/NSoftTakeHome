package com.nsoft.github.domain.model

import com.nsoft.github.presentation.ui.FirstScreen

/**
 * Class representing error states for the [FirstScreen]
 */
sealed class FirstScreenErrorState {
    /**
     * Value representing "no error" so we avoid using a stream with nullable values and "nulls"
     */
    object NoError : FirstScreenErrorState()

    /**
     * Value representing "no internet" error
     */
    object NoInternet: FirstScreenErrorState()

    /**
     * Value representing any other API error
     */
    object UnknownErrorHappened: FirstScreenErrorState()
}