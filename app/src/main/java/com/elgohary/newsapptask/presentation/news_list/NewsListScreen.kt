package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.designsystem.Strings
import com.elgohary.newsapptask.presentation.news_list.components.*

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
        onArticleClick = onArticleClick,
        onItemCountChanged = viewModel::onItemCountChanged
    )
}

@Composable
fun NewsListScreen(
    state: NewsListUiState,
    pagingItems: LazyPagingItems<Article>,
    onEvent: (NewsListEvent) -> Unit,
    onArticleClick: (Article) -> Unit,
    onItemCountChanged: (Int) -> Unit
) {
    LaunchedEffect(pagingItems.itemCount) {
        onItemCountChanged(pagingItems.itemCount)
    }

    Scaffold { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            if (state.isOffline) {
                OfflineBanner(onRetry = { pagingItems.refresh() })
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when (val refreshState = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> LoadingListPlaceholder()
                    is LoadState.Error -> ErrorScreen(
                        title = Strings.UnknownError,
                        message = refreshState.error.localizedMessage ?: Strings.UnknownError,
                        onRetry = { pagingItems.retry() }
                    )
                    is LoadState.NotLoading -> {
                        val totalCount = pagingItems.itemCount
                        if (totalCount == 0) {
                            EmptyScreen(title = Strings.EmptyNews, message = "")
                        } else {
                            LazyColumn(contentPadding = PaddingValues(bottom = Dimens.ListBottomPadding)) {
                                items(count = totalCount) { index ->
                                    val article = pagingItems[index] ?: return@items
                                    ArticleCard(
                                        article = article,
                                        onClick = {
                                            onArticleClick(article)
                                            onEvent(NewsListEvent.OnArticleClick(article))
                                        }
                                    )
                                }
                                if (state.gateActive) {
                                    item { TimedFooter() }
                                } else {
                                    when (val appendState = pagingItems.loadState.append) {
                                        is LoadState.Loading -> item { AppendLoadingIndicator() }
                                        is LoadState.Error -> item {
                                            ErrorScreen(
                                                title = Strings.LoadMoreFailed,
                                                message = appendState.error.localizedMessage ?: Strings.LoadMoreFailed,
                                                onRetry = { pagingItems.retry() }
                                            )
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
