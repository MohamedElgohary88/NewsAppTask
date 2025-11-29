package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.elgohary.newsapptask.domain.model.Article

@Composable
fun FavoritesRoute(
    onArticleClick: (Article) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    FavoritesScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onArticleClick = onArticleClick
    )
}
