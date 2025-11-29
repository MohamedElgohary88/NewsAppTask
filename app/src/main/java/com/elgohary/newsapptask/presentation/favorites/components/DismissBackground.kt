package com.elgohary.newsapptask.presentation.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val isDeleteSide = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd

    Column (
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight().width(30.dp)
            .background(
                if (isDeleteSide) MaterialTheme.colorScheme.errorContainer
                else Color.Transparent
            )
            .padding(horizontal = Dimens.SpacingXL, vertical = Dimens.SpacingMD),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isDeleteSide) {
            Icon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}