package com.elgohary.newsapptask.presentation.details.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun ArticleTitleAndMeta(title: String, publishedAt: String, sourceName: String?) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(Dimens.SpacingMD))
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!sourceName.isNullOrBlank()) {
            Text(
                text = sourceName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingLG))
        }
        Text(
            text = publishedAt,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}