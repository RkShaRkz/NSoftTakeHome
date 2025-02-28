package com.nsoft.github.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.nsoft.github.R
import com.nsoft.github.domain.model.GitCollaborator

@Composable
fun GitCollaboratorView(
    collaboratorToShow: GitCollaborator,
    modifier: Modifier = Modifier,
    favoritesButtonComposable: @Composable () -> Unit
) {
    // Lets try something simple. An avatar with the 'login' below it, with the favorites button at the bottom
    Column(
        modifier = modifier
    ) {
        // Avatar
        LoadImageFromUrl(
            imageUrl = collaboratorToShow.avatarUrl,
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_single),
                    end = dimensionResource(R.dimen.margin_single)
                )
                .size(
                    size = dimensionResource(R.dimen.firstscreen_avatarsize)
                )
                .align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.margin_single))
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = collaboratorToShow.login
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.margin_single))
        )
        // and the favorite composable
        favoritesButtonComposable()
    }
}
