package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.domain.model.Article

@Composable
fun NewsListRoute(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()

    NewsListScreen(
        state = state,
        pagingItems = pagingItems,
        onEvent = viewModel::onEvent,
        onArticleClick = onArticleClick
    )
}