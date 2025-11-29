package com.elgohary.newsapptask.presentation.details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R

@Composable
fun SaveFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(imageVector = Icons.Filled.Bookmark, contentDescription = stringResource(R.string.save_article), tint = MaterialTheme.colorScheme.onPrimary)
    }
}