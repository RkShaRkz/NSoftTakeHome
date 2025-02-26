package com.nsoft.github.presentation.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SideBySideTexts(
    leftText: String,
    rightText: String,
    modifier: Modifier = Modifier,
    spaceBetweenTexts: Dp
) {
    Row(
        modifier = Modifier
            .then(modifier)
    ) {
        Text(leftText)
        Spacer(modifier = Modifier.width(spaceBetweenTexts))
        Text(rightText)
    }
}
