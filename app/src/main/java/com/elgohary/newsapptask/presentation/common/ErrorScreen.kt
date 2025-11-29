package com.elgohary.newsapptask.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingXL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.SpacingSM)
        )
        if (onRetry != null) {
            Button(onClick = onRetry, modifier = Modifier.padding(top = Dimens.SpacingLG)) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}