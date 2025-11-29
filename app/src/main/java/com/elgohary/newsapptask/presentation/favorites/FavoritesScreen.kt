@file:OptIn(ExperimentalMaterialApi::class)

package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.designsystem.Strings
import com.elgohary.newsapptask.presentation.favorites.components.FavoriteSwipeToDeleteItem
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    onArticleClick: (Article) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subtle top padding
            Spacer(modifier = Modifier.height(8.dp))

            // Connectivity Banner
            if (state.isOffline) {
                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = "No Internet Connection - Showing Local Data",
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
                favorites = state.favorites,
                onArticleClick = onArticleClick,
                onDelete = { article ->
                    // 1. Notify ViewModel to delete
                    onEvent(FavoritesEvent.OnDeleteClick(article))

                    // 2. Show Snackbar
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = "Removed from favorites",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun FavoritesContent(
    favorites: List<Article>,
    onArticleClick: (Article) -> Unit,
    onDelete: (Article) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Empty State
        AnimatedVisibility(
            visible = favorites.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyScreen(title = Strings.EmptyNews, message = "No favorites yet")
        }

        // List State
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
                itemsIndexed(
                    items = favorites,
                    // Unique key handling to prevent UI glitches during deletion
                    key = { _, item -> item.url ?: item.title.hashCode() }
                ) { _, article ->
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