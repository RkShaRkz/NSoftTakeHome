package com.nsoft.github.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nsoft.github.R
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.NavigationRoutes
import com.nsoft.github.presentation.composables.GenericLazyColumnWithOverscroll
import com.nsoft.github.presentation.composables.GitRepoView
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
    val repos by presenter.repositoryListStream.collectAsState()
    MyLogger.d("SHARK", "[IN SCREEN] filtered repos count: ${repos.size}\tfiltered repos: ${repos}")

    // Handle navigation events
    HandleNavigationEvents(navigationEvents, navController, presenter)
    HandleErrorEvents(errorEvent, presenter)

    // The 'filter' input's text
    var filterText by remember { mutableStateOf("") }

    // Should run only once since the key will always be the same... i hope. Otherwise we'll think of something better.
    LaunchedEffect(Unit) {
        // Tell the presenter to fetch the repos
        presenter.getRepositories()
    }

    // And now, the UI code
    Column {

        // The 'filter' input box
        TextField(
            value = filterText,
            onValueChange = { newText ->
                filterText = newText
                presenter.setFilterCriteria(filterText)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter filter criteria") }
        )

        // The repos recyclerview
        GenericLazyColumnWithOverscroll(
            onOverScrollCallback = {
                presenter.fetchNextPage()
            },
            itemsList = repos,
            itemComposable = { index, gitRepo ->
                GitRepoView(
                    useExtendedView = false,
                    gitRepoToShow = gitRepo,
                    modifier = Modifier,
                    favoritesButtonClick = { presenter.toggleFavoriteStatus(gitRepo) },
                    favoritesButtonComposable = {
                        val isFavorite by presenter.isFavoriteFlow(gitRepo)
                            .collectAsState(initial = false)
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .padding(dimensionResource(R.dimen.margin_single))  //was double
                                .clickable {
                                    presenter.toggleFavoriteStatus(gitRepo)
                                }
                        )
                    },
                    openUrlButtonClick = {
                        // Nothing, not only is this part of the "extended" view, but it's not a feature of the first screen
                    }
                )
            },
            modifier = Modifier,
            useDivider = true,
            dividerThickness = dimensionResource(R.dimen.margin_single),
            dividerColor = Color.Black
        )
    }
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
