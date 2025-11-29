package com.elgohary.newsapptask.presentation.news_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.elgohary.newsapptask.presentation.designsystem.AppColors
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.designsystem.Strings

@Composable
fun OfflineBanner(onRetry: () -> Unit) {
    Surface(color = AppColors.OfflineRed) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.SpacingSM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = Strings.NoInternetTitle, color = Color.White)
            Text(
                text = Strings.Retry,
                color = Color.White,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}

