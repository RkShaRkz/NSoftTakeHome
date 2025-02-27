package com.nsoft.github.domain.navigation

import com.nsoft.github.presentation.ui.SecondScreen

/**
 * Enum class representing different navigation events from the [SecondScreen]
 */
enum class SecondScreenNavigationEvent {
    /**
     * Enum value representing "nowhere" or rather, no navigation event (remaining on this screen)
     * We use this one instead of making the navigation stream use a nullable event and using [null]
      */
    NOWHERE,

    /**
     * Value representing that we should navigate to "third" screen
     */
    THIRD_SCREEN,

    /**
     * Value representing that we should navigate to repository's html_url
     */
    PROJECT_URL
}
