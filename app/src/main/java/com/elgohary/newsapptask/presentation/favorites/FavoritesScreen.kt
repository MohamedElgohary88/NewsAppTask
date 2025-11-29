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
import com.elgohary.newsapptask.presentation.common.ErrorScreen
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
            Spacer(modifier = Modifier.height(8.dp))
            if (state.isOffline) {
                ErrorScreen(
                    title = Strings.NoInternetTitle,
                    message = Strings.CheckYourConnection,
                    onRetry = { onEvent(FavoritesEvent.OnRetry) }
                )
            }

            FavoritesContent(
                favorites = state.favorites,
                onArticleClick = onArticleClick,
                onDelete = { article ->
                    onEvent(FavoritesEvent.OnDeleteClick(article))
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
        AnimatedVisibility(
            visible = favorites.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyScreen(title = Strings.EmptyNews, message = "No favorites yet")
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
                itemsIndexed(
                    items = favorites,
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