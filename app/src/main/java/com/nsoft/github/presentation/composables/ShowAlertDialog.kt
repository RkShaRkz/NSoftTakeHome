package com.nsoft.github.presentation.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Deprecated(message = "This should be replaced eventually with something better")
@Composable
fun ShowAlertDialog(
    buttonText: String,
    errorMessage: String,
    onDismissRequest: () -> Unit
) {
    return AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(buttonText)
            }
        },
        text = { Text(errorMessage) }
    )
}