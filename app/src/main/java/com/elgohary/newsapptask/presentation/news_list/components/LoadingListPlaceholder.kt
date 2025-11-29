package com.elgohary.newsapptask.presentation.news_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.common.ShimmerEffect
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun LoadingListPlaceholder() {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight)
                    .padding(horizontal = Dimens.SpacingMD, vertical = Dimens.SpacingSM)
            ) { ShimmerEffect(modifier = Modifier.fillMaxSize()) }
        }
    }
}

