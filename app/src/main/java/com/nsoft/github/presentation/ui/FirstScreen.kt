package com.nsoft.github.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nsoft.github.R
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.NavigationRoutes
import com.nsoft.github.presentation.composables.ShowAlertDialog
import com.nsoft.github.presentation.viewmodel.FirstScreenViewModel
import com.nsoft.github.util.MyLogger
import com.nsoft.github.util.exhaustive

@Composable
fun FirstScreen(navController: NavHostController) {
    // Initialize values we'll need and be using for this screen
    val presenter: FirstScreenViewModel = hiltViewModel<FirstScreenViewModel>()

    // Apparently this is more preferred than using a LiveData for this.
    val navigationEvents by presenter.navigationStream.collectAsState()
    val errorEvent by presenter.errorStream.collectAsState()

    // Start listening to viewmodel streams
    val repos by presenter.repositoryListStream.observeAsState()

    // Handle navigation events
    HandleNavigationEvents(navigationEvents, navController, presenter)
    HandleErrorEvents(errorEvent, presenter)

    // Tell the presenter to fetch the repos
    presenter.getRepositories()
    MyLogger.wtf("SHARK", "repos in FirstScreen: ${repos}")
}



/**
 * Method (composable) that handles navigation events, and moves away from the [FirstScreen] to other screens
 *
 * @param navigationEvents the event to respond to and navigate accordingly
 * @param navController the [NavHostController] to use for navigation
 * @param presenter the presenter to use for invoking further presentation logic on
 * @see FirstScreenNavigationEvent
 * @see FirstScreenViewModel
 */
@Composable
private fun HandleNavigationEvents(
    navigationEvents: FirstScreenNavigationEvent,
    navController: NavHostController,
    presenter: FirstScreenViewModel
) {
    LaunchedEffect(navigationEvents) {
        when (navigationEvents) {
            FirstScreenNavigationEvent.NOWHERE -> {
                // We do nothing here, since this value is mostly just a "just in case" placeholder
            }

            FirstScreenNavigationEvent.SECOND_SCREEN -> {
                // We navigate back to the home screen, clearing up the backstack
                navController.navigate(NavigationRoutes.SECOND_SCREEN.getRouteName()) {
                    //TODO decide on this one later when you see how the app actually works out and whether this makes sense
//                    popUpTo(NavigationRoutes.SECOND_SCREEN.getRouteName()) { inclusive = true }
                }
            }
        }.exhaustive
        // And tell the presenter we're done navigating so it can clear the last navigation value
        presenter.doneNavigating()
    }
}

@Composable
private fun HandleErrorEvents(
    errorEvent: FirstScreenErrorState,
    presenter: FirstScreenViewModel
) {
    return when (errorEvent) {
        FirstScreenErrorState.NoError -> {
            // This is a simple "no error" state, and as such nothing has to be done here
        }

        FirstScreenErrorState.NoInternet -> {
            // Show a generic alert dialog saying "no internet"
            //TODO replace with something better eventually
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.no_internet_available),
                onDismissRequest = { presenter.errorHandled() }
            )
        }

        FirstScreenErrorState.UnknownErrorHappened -> {
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.whoops_something_went_wrong),
                onDismissRequest = { presenter.errorHandled() }
            )
        }
    }.exhaustive
}
