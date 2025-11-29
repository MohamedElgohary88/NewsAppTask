@file:OptIn(ExperimentalMaterialApi::class)

package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onArticleClick: (Article) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val connectivityStatus by viewModel.connectivityStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        backgroundColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // subtle top padding so first card is fully visible under system bars
            Spacer(modifier = Modifier.height(8.dp))

            // connectivity banner
            if (connectivityStatus != ConnectivityObserver.Status.Available) {
                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = "No Internet Connection",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            FavoritesContent(
                favorites = favorites,
                onArticleClick = onArticleClick,
                onDelete = { article ->
                    // Persistently delete from local DB via ViewModel
                    viewModel.deleteArticle(article)
                    scope.launch {
                        snackbarHostState.showSnackbar("Removed from favorites")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    favorites: List<Article>,
    onArticleClick: (Article) -> Unit,
    onDelete: (Article) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = favorites.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyScreen(message = "No favorites yet")
        }

        AnimatedVisibility(
            visible = favorites.isNotEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 8 })
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = favorites,
                    key = { it.url ?: it.title ?: it.hashCode().toString() }
                ) { article ->
                    FavoriteSwipeToDeleteItem(
                        article = article,
                        onClick = { onArticleClick(article) },
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteSwipeToDeleteItem(
    article: Article,
    onClick: () -> Unit,
    onDelete: (Article) -> Unit
) {
    val currentItem by rememberUpdatedState(article)
    var isRemoved by remember { mutableStateOf(false) }

    // Once removed, drop the composable from the tree quickly
    if (isRemoved) return

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                // Only enable one direction: swipe from end to start (right-to-left)
                SwipeToDismissBoxValue.EndToStart -> {
                    isRemoved = true
                    onDelete(currentItem)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        enableDismissFromStartToEnd = false, // disable one side
        enableDismissFromEndToStart = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
    ) {
        ArticleCard(
            article = article,
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    // Show red container and icon only on the delete side, and keep it
    // visually "next to" the card by aligning to the swipe direction.
    val isDeleteSide = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDeleteSide) MaterialTheme.colorScheme.errorContainer
                else Color.Transparent
            )
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
