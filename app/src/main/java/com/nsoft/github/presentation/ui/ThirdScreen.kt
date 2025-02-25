package com.nsoft.github.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nsoft.github.R
import com.nsoft.github.domain.model.ThirdScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.ThirdScreenNavigationEvent
import com.nsoft.github.presentation.composables.ShowAlertDialog
import com.nsoft.github.presentation.viewmodel.FirstScreenViewModel
import com.nsoft.github.presentation.viewmodel.ThirdScreenViewModel
import com.nsoft.github.util.exhaustive

@Composable
fun ThirdScreen(navController: NavHostController) {
    // Initialize values we'll need and be using for this screen
    val presenter: ThirdScreenViewModel = hiltViewModel<ThirdScreenViewModel>()

    // Apparently this is more preferred than using a LiveData for this.
    val navigationEvents by presenter.navigationStream.collectAsState()
    val errorEvent by presenter.errorStream.collectAsState()

    // Start listening to viewmodel streams

    // Handle navigation events
    HandleNavigationEvents(navigationEvents, navController, presenter)
    HandleErrorEvents(errorEvent, presenter)
}



/**
 * Method (composable) that handles navigation events, and moves away from the [SecondScreen] to other screens
 *
 * @param navigationEvents the event to respond to and navigate accordingly
 * @param navController the [NavHostController] to use for navigation
 * @param presenter the presenter to use for invoking further presentation logic on
 * @see FirstScreenNavigationEvent
 * @see FirstScreenViewModel
 */
@Composable
private fun HandleNavigationEvents(
    navigationEvents: ThirdScreenNavigationEvent,
    navController: NavHostController,
    presenter: ThirdScreenViewModel
) {
    LaunchedEffect(navigationEvents) {
        when (navigationEvents) {
            ThirdScreenNavigationEvent.NOWHERE -> {
                // We do nothing here, since this value is mostly just a "just in case" placeholder
            }

        }.exhaustive
        // And tell the presenter we're done navigating so it can clear the last navigation value
        presenter.doneNavigating()
    }
}

@Composable
private fun HandleErrorEvents(
    errorEvent: ThirdScreenErrorState,
    presenter: ThirdScreenViewModel
) {
    return when (errorEvent) {
        ThirdScreenErrorState.NoError -> {
            // This is a simple "no error" state, and as such nothing has to be done here
        }

        ThirdScreenErrorState.NoInternet -> {
            // Show a generic alert dialog saying "no internet"
            //TODO replace with something better eventually
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.no_internet_available),
                onDismissRequest = { presenter.errorHandled() }
            )
        }

        ThirdScreenErrorState.UnknownErrorHappened -> {
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.whoops_something_went_wrong),
                onDismissRequest = { presenter.errorHandled() }
            )
        }
    }.exhaustive
}