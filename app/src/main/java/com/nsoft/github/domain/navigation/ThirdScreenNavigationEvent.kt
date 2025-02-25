package com.nsoft.github.domain.navigation

import com.nsoft.github.presentation.ui.ThirdScreen

/**
 * Enum class representing different navigation events from the [ThirdScreen]
 */
enum class ThirdScreenNavigationEvent {
    /**
     * Enum value representing "nowhere" or rather, no navigation event (remaining on this screen)
     * We use this one instead of making the navigation stream use a nullable event and using [null]
      */
    NOWHERE
}