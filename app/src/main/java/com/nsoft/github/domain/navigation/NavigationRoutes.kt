package com.nsoft.github.domain.navigation

import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost

private const val FIRST_ROUTE_NAME = "first"
private const val SECOND_ROUTE_NAME = "second"
private const val THIRD_ROUTE_NAME = "third"

/**
 * Class holding all navigation-related strings used for navigating to different screens in the app
 *
 * **WARNING** You **HAVE** to use the [getRouteName] method when using this with
 * [NavHost], [NavGraph], [NavGraphBuilder] or anything from the "navigation" framework.
 */
enum class NavigationRoutes(private val routeName: String) {
    FIRST_SCREEN(FIRST_ROUTE_NAME),
    SECOND_SCREEN(SECOND_ROUTE_NAME),
    THIRD_SCREEN(THIRD_ROUTE_NAME);

    /**
     * Method for getting this navigation route's name (destination)
     * @return navigation route's name, as string
     */
    fun getRouteName(): String {
        return routeName
    }
}
