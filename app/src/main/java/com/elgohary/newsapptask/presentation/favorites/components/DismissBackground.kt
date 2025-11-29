package com.elgohary.newsapptask.presentation.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R
import androidx.compose.ui.graphics.Color

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val isDeleteSide = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDeleteSide) MaterialTheme.colorScheme.errorContainer
                else Color.Transparent
            )
            .padding(horizontal = Dimens.SpacingXL, vertical = Dimens.SpacingMD),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (isDeleteSide) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}