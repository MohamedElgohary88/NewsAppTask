package com.elgohary.newsapptask.presentation.news_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.presentation.designsystem.AppColors
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun OfflineBanner(onRetry: () -> Unit) {
    Surface(color = AppColors.OfflineRed) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.SpacingSM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.no_internet_title), color = Color.White)
            Text(
                text = stringResource(R.string.retry),
                color = Color.White,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}
