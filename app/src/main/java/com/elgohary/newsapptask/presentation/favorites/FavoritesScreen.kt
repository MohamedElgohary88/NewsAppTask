@file:OptIn(ExperimentalMaterialApi::class)

package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (connectivityStatus != ConnectivityObserver.Status.Available) {
                Surface(color = Color.Red.copy(alpha = 0.9f)) {
                    Text(
                        text = "No Internet Connection",
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        color = Color.White
                    )
                }
            }
            FavoritesContent(
                favorites = favorites,
                onArticleClick = onArticleClick,
                onDelete = { article ->
                    viewModel.deleteArticle(article)
                    scope.launch { snackbarHostState.showSnackbar(message = "Removed from favorites") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    favorites: List<Article>,
    onArticleClick: (Article) -> Unit,
    onDelete: (Article) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = favorites.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
            EmptyScreen(message = "No favorites yet")
        }
        AnimatedVisibility(visible = favorites.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
            LazyColumn(contentPadding = PaddingValues(bottom = 12.dp)) {
                items(
                    items = favorites,
                    key = { it.url ?: it.title ?: it.hashCode().toString() }
                ) { article ->
                    val isRenderable = !(article.title.isNullOrBlank() && article.description.isNullOrBlank() && article.urlToImage.isNullOrBlank())
                    if (isRenderable) {
                        val currentItem by rememberUpdatedState(article)
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                when (value) {
                                    SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> {
                                        onDelete(currentItem)
                                        true
                                    }
                                    SwipeToDismissBoxValue.Settled -> false
                                }
                            },
                            positionalThreshold = { it * 0.25f }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { DismissBackground(dismissState) },
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = true
                        ) {
                            ArticleCard(
                                article = article,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onArticleClick(article) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primaryContainer
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Filled.Delete, contentDescription = "delete")
        Spacer(modifier = Modifier)
        Icon(Icons.Filled.Delete, contentDescription = "archive")
    }
}
