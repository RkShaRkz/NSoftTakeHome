package com.nsoft.github.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.nsoft.github.R
import com.nsoft.github.domain.model.GitRepository
import com.nsoft.github.util.PATTERN_DDMMYYYYatHHMM
import com.nsoft.github.util.formatToPattern


@Composable
fun GitRepoView(
    useExtendedView: Boolean = false,
    gitRepoToShow: GitRepository,
    modifier: Modifier = Modifier,
    favoritesButtonComposable: @Composable () -> Unit,
    openUrlButtonClick: () -> Unit = {},
    contributorsComposable: @Composable () -> Unit = {},
) {
    // Ok, so the idea is:
    // avater on the left, followed by a bunch of lines on the right
    // all surrounded by a single margin
    Row(
        modifier = Modifier
            .padding(
                dimensionResource(R.dimen.margin_single)
            )
            .then(modifier)
    ) {
        LoadImageFromUrl(
            imageUrl = gitRepoToShow.owner.avatarUrl,
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_single),
                    end = dimensionResource(R.dimen.margin_single)
                )
                .size(
                    size = dimensionResource(R.dimen.firstscreen_avatarsize)
                )
                .align(Alignment.CenterVertically)
        )

        // Now the column containing a bunch of text
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            // login
            SideBySideTexts(
                leftText = "Owner login:",
                rightText = gitRepoToShow.owner.login,
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            // repo name
            SideBySideTexts(
                leftText = "Repo name:",
                rightText = gitRepoToShow.repoName,
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            // description
            SideBySideTexts(
                leftText = "Description:",
                rightText = gitRepoToShow.description,
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            //language
            SideBySideTexts(
                leftText = "Language:",
                rightText = gitRepoToShow.language,
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            //stargazers
            SideBySideTexts(
                leftText = "Stargazers count:",
                rightText = gitRepoToShow.stargazersCount.toString(),
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            //forks count
            SideBySideTexts(
                leftText = "Forks count:",
                rightText = gitRepoToShow.forksCount.toString(),
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            // open issues
            SideBySideTexts(
                leftText = "Open Issues:",
                rightText = gitRepoToShow.openIssues.toString(),
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            // watchers count
            SideBySideTexts(
                leftText = "Watchers count:",
                rightText = gitRepoToShow.watchersCount.toString(),
                spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
            )
            /*
             * Extended views here
             */
            if (useExtendedView) {
                // default branch
                SideBySideTexts(
                    leftText = "Default branch:",
                    rightText = gitRepoToShow.defaultBranch,
                    spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
                )
                // created at
                SideBySideTexts(
                    leftText = "Created at:",
                    rightText = gitRepoToShow.createdAt.formatToPattern(PATTERN_DDMMYYYYatHHMM),
                    spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
                )
                // updated at
                SideBySideTexts(
                    leftText = "Updated at:",
                    rightText = gitRepoToShow.updatedAt.formatToPattern(PATTERN_DDMMYYYYatHHMM),
                    spaceBetweenTexts = dimensionResource(R.dimen.margin_double),
                )
                // For these two, lets not use the SideBySideTexts, since we need to render images ...
                Column(
                    modifier = Modifier
                        .then(modifier)
                ) {
                    Text("Contributors URL:")
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_single)))
                    contributorsComposable()
                    // And add another spacer so it looks better when the collaborators are loaded
                    Spacer(
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.margin_single))
                    )
                }
            }

            // Now, the two buttons
            // one for adding/removing the repo to list of favorites
            // and the second one - available only in extended view - for opening the html_url
            Row {
                Box {
                    // Instead of a button, lets let the caller provide the whole composable to be
                    // used for clicking instead
                    favoritesButtonComposable()
                }

                if (useExtendedView) {
                    Button(
                        onClick = openUrlButtonClick,
                    ) {
                        Text(text = stringResource(R.string.open))
                    }
                }
            }
        }
    }
}
