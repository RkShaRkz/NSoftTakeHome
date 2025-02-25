package com.nsoft.github.domain.model

import com.nsoft.github.presentation.ui.SecondScreen

/**
 * Class representing error states for the [SecondScreen]
 */
sealed class SecondScreenErrorState {
    /**
     * Value representing "no error" so we avoid using a stream with nullable values and "nulls"
     */
    object NoError : SecondScreenErrorState()

    /**
     * Value representing "no internet" error
     */
    object NoInternet: SecondScreenErrorState()

    /**
     * Value representing any other API error
     */
    object UnknownErrorHappened: SecondScreenErrorState()
}