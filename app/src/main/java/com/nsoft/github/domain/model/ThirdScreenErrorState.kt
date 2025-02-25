package com.nsoft.github.domain.model

import com.nsoft.github.presentation.ui.ThirdScreen

/**
 * Class representing error states for the [ThirdScreen]
 */
sealed class ThirdScreenErrorState {
    /**
     * Value representing "no error" so we avoid using a stream with nullable values and "nulls"
     */
    object NoError : ThirdScreenErrorState()

    /**
     * Value representing "no internet" error
     */
    object NoInternet: ThirdScreenErrorState()

    /**
     * Value representing any other API error
     */
    object UnknownErrorHappened: ThirdScreenErrorState()
}