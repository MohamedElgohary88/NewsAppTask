package com.elgohary.newsapptask.presentation.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    // Show red container and icon only on the delete side, and keep it
    // visually "next to" the card by aligning to the swipe direction.
    val isDeleteSide = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDeleteSide) MaterialTheme.colorScheme.errorContainer
                else Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (isDeleteSide) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}