package com.elgohary.newsapptask.presentation.details.components

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun ArticleDescription(
    description: String?,
    content: String?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    articleUrl: String?
) {
    val context = LocalContext.current
    val descSafe = description.orEmpty()
    val contentSafe = content.orEmpty()

    val collapsedCharLimit = 220
    val needsCollapse = descSafe.length > collapsedCharLimit && !isExpanded
    val visibleText = if (needsCollapse) descSafe.take(collapsedCharLimit) + "…" else descSafe

    Text(
        text = visibleText,
        style = MaterialTheme.typography.bodyMedium
    )

    if (needsCollapse || (!isExpanded && contentSafe.isNotBlank() && descSafe.isNotBlank())) {
        ReadMoreLink(onClick = onToggleExpand)
    }

    AnimatedVisibility(visible = isExpanded) {
        Column {
            if (contentSafe.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = contentSafe,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!articleUrl.isNullOrBlank()) {
                OpenInBrowserLink(onOpen = {
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(context, articleUrl.toUri())
                })
            }
        }
    }
}

@Composable
private fun ReadMoreLink(onClick: () -> Unit) {
    Text(
        text = "Read more",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Read more" }
    )
}

@Composable
private fun OpenInBrowserLink(onOpen: () -> Unit) {
    Text(
        text = "Open in browser",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = onOpen)
            .semantics { contentDescription = "Open article in browser" }
    )
}