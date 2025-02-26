package com.nsoft.github.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import coil.compose.rememberAsyncImagePainter

@Composable
fun LoadImageFromUrl(imageUrl: String) {
    val painter = rememberAsyncImagePainter(model = imageUrl)

    Image(
        painter = painter,
        contentDescription = "Loaded image from URL"
    )
}
