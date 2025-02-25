package com.nsoft.github.domain.navigation

import com.nsoft.github.presentation.ui.FirstScreen

/**
 * Enum class representing different navigation events from the [FirstScreen]
 */
enum class FirstScreenNavigationEvent {
    /**
     * Enum value representing "nowhere" or rather, no navigation event (remaining on this screen)
     * We use this one instead of making the navigation stream use a nullable event and using [null]
      */
    NOWHERE,

    /**
     * Value representing that we should navigate to "second" screen
     */
    SECOND_SCREEN
}