package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.elgohary.newsapptask.domain.model.Article
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FavoritesRoute(
    onArticleClick: (Article) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    // Collect one-off UI events from the ViewModel and act on them (navigation, messages)
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is FavoritesEvent.NavigateToArticle -> onArticleClick(ev.article)
                is FavoritesEvent.ShowMessage -> scope.launch { /* show snackbar if desired */ }
                is FavoritesEvent.RetryRequested -> { /* handled by ViewModel or UI if needed */ }
                else -> Unit
            }
        }
    }

    FavoritesScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
