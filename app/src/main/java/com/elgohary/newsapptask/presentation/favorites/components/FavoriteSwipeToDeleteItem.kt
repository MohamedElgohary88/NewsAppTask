package com.elgohary.newsapptask.presentation.favorites.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard

@Composable
fun FavoriteSwipeToDeleteItem(
    article: Article,
    onClick: () -> Unit,
    onDelete: (Article) -> Unit
) {
    val currentItem by rememberUpdatedState(article)

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(currentItem)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
    ) {
        ArticleCard(article = article, modifier = Modifier.fillMaxWidth(), onClick = onClick)
    }
}