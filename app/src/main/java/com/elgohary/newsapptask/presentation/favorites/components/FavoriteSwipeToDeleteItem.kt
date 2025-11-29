package com.elgohary.newsapptask.presentation.favorites.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard

@Composable
fun FavoriteSwipeToDeleteItem(
    article: Article,
    onClick: () -> Unit,
    onDelete: (Article) -> Unit
) {
    val currentItem by rememberUpdatedState(article)
    var isRemoved by remember { mutableStateOf(false) }
    if (isRemoved) return

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    isRemoved = true
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

@Composable
private fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val isDeleteSide = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDeleteSide) MaterialTheme.colorScheme.errorContainer else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (isDeleteSide) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
