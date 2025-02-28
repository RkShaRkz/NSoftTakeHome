package com.nsoft.github.presentation.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nsoft.github.R
import com.nsoft.github.data.local.UriMaker
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.NavigationRoutes
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import com.nsoft.github.presentation.composables.GitCollaboratorView
import com.nsoft.github.presentation.composables.GitRepoView
import com.nsoft.github.presentation.composables.ShowAlertDialog
import com.nsoft.github.presentation.viewmodel.FirstScreenViewModel
import com.nsoft.github.presentation.viewmodel.SecondScreenViewModel
import com.nsoft.github.util.MyLogger
import com.nsoft.github.util.exhaustive

@Composable
fun SecondScreen(navController: NavHostController) {
    // Initialize values we'll need and be using for this screen
    val presenter: SecondScreenViewModel = hiltViewModel<SecondScreenViewModel>()

    // Apparently this is more preferred than using a LiveData for this.
    val navigationEvents by presenter.navigationStream.collectAsState()
    val errorEvent by presenter.errorStream.collectAsState()

    // Start listening to viewmodel streams
    val contributors by presenter.contributorsListStream.collectAsState()
    val collaborators by presenter.collaboratorsListStream.collectAsState()
    MyLogger.e("SHARK", "contributors size: ${contributors.size}\tcollaborators size: ${collaborators.size}")   //TODO remove

    // Handle navigation events
    HandleNavigationEvents(navigationEvents, navController, presenter)
    HandleErrorEvents(errorEvent, presenter)

    val gitRepo = presenter.getClickedRepo()
    Column {
        Row {
            Button(
                onClick = { presenter.getRepoDetails() }
            ) { Text("Get Repo Details") }
        }
        GitRepoView(
            useExtendedView = true,
            gitRepoToShow = gitRepo,
            modifier = Modifier,
            favoritesButtonComposable = {
                val isFavorite by presenter.isFavoriteRepository(gitRepo)
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
            openUrlButtonClick = { presenter.onUrlButtonClicked(gitRepo) },
            contributorsComposable = {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(contributors) { index: Int, item: GitCollaborator ->
                        GitCollaboratorView(
                            collaboratorToShow = item,
                            modifier = Modifier,
                            favoritesButtonComposable = {
                                val isFavorite by presenter.isFavoriteCollaborator(item)
                                    .collectAsState(initial = false)
                                Icon(imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .clickable {
                                            presenter.toggleCollaboratorFavoriteStatus(item)
                                        })
                            }
                        )
                    }
                }
            }
        )
    }
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
    navigationEvents: SecondScreenNavigationEvent,
    navController: NavHostController,
    presenter: SecondScreenViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(navigationEvents) {
        when (navigationEvents) {
            SecondScreenNavigationEvent.NOWHERE -> {
                // We do nothing here, since this value is mostly just a "just in case" placeholder
            }

            SecondScreenNavigationEvent.THIRD_SCREEN -> {
                // We navigate back to the home screen, clearing up the backstack
                navController.navigate(NavigationRoutes.THIRD_SCREEN.getRouteName()) {
                    //TODO decide on this one later when you see how the app actually works out and whether this makes sense
//                    popUpTo(NavigationRoutes.THIRD_SCREEN.getRouteName()) { inclusive = true }
                }
            }

            SecondScreenNavigationEvent.PROJECT_URL -> {
                val url = UriMaker.createUri(presenter.getDestinationUrlString())
                val intent = Intent(Intent.ACTION_VIEW, url)
                context.startActivity(intent)
            }
        }.exhaustive
        // And tell the presenter we're done navigating so it can clear the last navigation value
        presenter.doneNavigating()
    }
}

@Composable
private fun HandleErrorEvents(
    errorEvent: SecondScreenErrorState,
    presenter: SecondScreenViewModel
) {
    return when (errorEvent) {
        SecondScreenErrorState.NoError -> {
            // This is a simple "no error" state, and as such nothing has to be done here
        }

        SecondScreenErrorState.NoInternet -> {
            // Show a generic alert dialog saying "no internet"
            //TODO replace with something better eventually
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.no_internet_available),
                onDismissRequest = { presenter.errorHandled() }
            )
        }

        SecondScreenErrorState.UnknownErrorHappened -> {
            ShowAlertDialog(
                buttonText = stringResource(R.string.ok),
                errorMessage = stringResource(R.string.whoops_something_went_wrong),
                onDismissRequest = { presenter.errorHandled() }
            )
        }
    }.exhaustive
}
