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
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.designsystem.common.EmptyScreen
import com.elgohary.newsapptask.presentation.designsystem.common.ErrorScreen
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.favorites.components.FavoriteSwipeToDeleteItem
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Precompute strings in composable scope so they can be used inside non-composable lambdas
    val removedMsg = stringResource(R.string.removed_from_favorites)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpacingSM))
            if (state.isOffline) {
                ErrorScreen(
                    title = stringResource(R.string.no_internet_title),
                    message = stringResource(R.string.check_your_connection),
                    onRetry = { onEvent(FavoritesEvent.OnRetry) }
                )
            }

            FavoritesContent(
                favorites = state.favorites,
                onArticleClick = { article -> onEvent(FavoritesEvent.OnArticleClick(article)) },
                onDelete = { article ->
                    onEvent(FavoritesEvent.OnDeleteClick(article))
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = removedMsg,
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
            EmptyScreen(title = stringResource(R.string.empty_news), message = stringResource(R.string.no_favorites_yet))
        }
        AnimatedVisibility(
            visible = favorites.isNotEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 8 })
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimens.SpacingLG, vertical = Dimens.SpacingSM),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLG)
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
