@file:OptIn(ExperimentalMaterialApi::class)

package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.designsystem.Strings.EmptyNews
import com.elgohary.newsapptask.presentation.favorites.components.FavoriteSwipeToDeleteItem
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onArticleClick: (Article) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
            // subtle top padding so first card is fully visible under system bars
            Spacer(modifier = Modifier.height(8.dp))

            // connectivity banner
            if (!uiState.isConnected) {
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
                favorites = uiState.favorites,
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
            EmptyScreen(title = EmptyNews, message = "No favorites yet")
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
                    key = { index, item -> item.url ?: item.title ?: "fav_$index" }
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