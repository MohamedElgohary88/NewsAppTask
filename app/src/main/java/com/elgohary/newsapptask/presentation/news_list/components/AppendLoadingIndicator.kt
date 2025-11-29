package com.elgohary.newsapptask.presentation.news_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun AppendLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SpacingLG),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

