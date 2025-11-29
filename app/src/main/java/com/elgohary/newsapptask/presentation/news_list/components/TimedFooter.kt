package com.elgohary.newsapptask.presentation.news_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun TimedFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingLG, vertical = Dimens.SpacingSM),
        verticalAlignment = Alignment.CenterVertically
    ) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
}

