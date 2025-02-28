package com.nsoft.github.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nsoft.github.R
import com.nsoft.github.domain.model.ThirdScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.ThirdScreenNavigationEvent
import com.nsoft.github.presentation.composables.GenericLazyColumnWithOverscroll
import com.nsoft.github.presentation.composables.GitCollaboratorView
import com.nsoft.github.presentation.composables.GitRepoView
import com.nsoft.github.presentation.composables.ShowAlertDialog
import com.nsoft.github.presentation.viewmodel.FirstScreenViewModel
import com.nsoft.github.presentation.viewmodel.ThirdScreenViewModel
import com.nsoft.github.util.exhaustive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(navController: NavHostController) {
    // Initialize values we'll need and be using for this screen
    val presenter: ThirdScreenViewModel = hiltViewModel<ThirdScreenViewModel>()

    // Apparently this is more preferred than using a LiveData for this.
    val navigationEvents by presenter.navigationStream.collectAsState()
    val errorEvent by presenter.errorStream.collectAsState()

    // Start listening to viewmodel streams
    val selectedTabIndex = presenter.selectedTabIndex.collectAsState()

    // Handle navigation events
    HandleNavigationEvents(navigationEvents, navController, presenter)
    HandleErrorEvents(errorEvent, presenter)

    // Followed by the UI code
    val tabs = listOf(stringResource(R.string.repositories), stringResource(R.string.contributors))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.favorites))
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            TabRow(selectedTabIndex = selectedTabIndex.value) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex.value == index,
                        onClick = {
                                presenter.onTabSelected(index)
                        }
                    )
                }
            }
            when (selectedTabIndex.value) {
                0 -> Tab1Content()
                1 -> Tab2Content()
            }
        }
    }
}

@Composable
fun Tab1Content() {
    // Content for Tab 1 - repos
    // Get the viewmodel and start listening to the repo stream
    val presenter: ThirdScreenViewModel = hiltViewModel<ThirdScreenViewModel>()
    val repos by presenter.repositoryListStream.collectAsState()

    // The repos recyclerview
    GenericLazyColumnWithOverscroll(
        onOverScrollCallback = { },
        itemsList = repos,
        itemComposable = { index, gitRepo ->
            GitRepoView(
                useExtendedView = false,
                gitRepoToShow = gitRepo,
                modifier = Modifier,
                favoritesButtonComposable = {
                    val isFavorite by presenter.isFavoriteRepositoryFlow(gitRepo)
                        .collectAsState(initial = false)
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.margin_single))  //was double
                            .clickable {
                                presenter.toggleRepositoryFavoriteStatus(gitRepo)
                            }
                    )
                },
            )
        },
        modifier = Modifier,
        useDivider = true,
        dividerThickness = dimensionResource(R.dimen.margin_single),
        dividerColor = Color.Black
    )
}

@Composable
fun Tab2Content() {
    // Content for Tab 2 - contributors
    // Get the viewmodel and start listening to the collab stream
    val presenter: ThirdScreenViewModel = hiltViewModel<ThirdScreenViewModel>()
    val collaborators by presenter.collaboratorsListStream.collectAsState()

    // The repos recyclerview
    GenericLazyColumnWithOverscroll(
        onOverScrollCallback = { },
        itemsList = collaborators,
        itemComposable = { index, collaborator ->
            GitCollaboratorView(
                collaboratorToShow = collaborator,
                modifier = Modifier,
                favoritesButtonComposable = {
                    val isFavorite by presenter.isFavoriteCollaborator(collaborator)
                        .collectAsState(initial = false)
                    Icon(imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier
//                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                presenter.toggleCollaboratorFavoriteStatus(collaborator)
                            })
                }
            )
        },
        modifier = Modifier,
        useDivider = true,
        dividerThickness = dimensionResource(R.dimen.margin_single),
        dividerColor = Color.Black
    )
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
