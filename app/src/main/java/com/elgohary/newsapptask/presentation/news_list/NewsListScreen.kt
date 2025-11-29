package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.designsystem.Strings
import com.elgohary.newsapptask.presentation.news_list.components.*

@Composable
fun NewsListScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    // Notify VM of item count changes (no business logic here)
    LaunchedEffect(pagingItems.itemCount) { viewModel.onItemCountChanged(pagingItems.itemCount) }

    Scaffold { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            if (!uiState.isConnected) {
                OfflineBanner(onRetry = { pagingItems.refresh() })
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> LoadingListPlaceholder()
                    is LoadState.Error -> ErrorScreen(
                        title = Strings.UnknownError,
                        message = state.error.localizedMessage ?: Strings.UnknownError,
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
                                    ArticleCard(article = article, onClick = { onArticleClick(article) })
                                }
                                if (uiState.gateActive) {
                                    item { TimedFooter() }
                                } else {
                                    when (pagingItems.loadState.append) {
                                        is LoadState.Loading -> item { AppendLoadingIndicator() }
                                        is LoadState.Error -> {
                                            val appendError = pagingItems.loadState.append as LoadState.Error
                                            item {
                                                ErrorScreen(
                                                    title = Strings.LoadMoreFailed,
                                                    message = appendError.error.localizedMessage ?: Strings.LoadMoreFailed,
                                                    onRetry = { pagingItems.retry() }
                                                )
                                            }
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
