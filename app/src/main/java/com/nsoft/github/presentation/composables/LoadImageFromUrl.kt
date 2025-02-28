package com.nsoft.github.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter

@Composable
fun LoadImageFromUrl(
    imageUrl: String,
    modifier: Modifier
) {
    val painter = rememberAsyncImagePainter(model = imageUrl)

    Image(
        painter = painter,
        contentDescription = "Loaded image from URL",
        modifier = modifier
    )
}
