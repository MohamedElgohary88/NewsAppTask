package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.domain.model.Article
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NewsListRoute(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is NewsListEvent.OnArticleClick -> onArticleClick(ev.article)
                is NewsListEvent.OnRetry -> viewModel.onEvent(NewsListEvent.OnRetry)
                else -> Unit
            }
        }
    }

    NewsListScreen(
        state = state,
        pagingItems = pagingItems,
        onEvent = viewModel::onEvent,
        onArticleClick = onArticleClick
    )
}