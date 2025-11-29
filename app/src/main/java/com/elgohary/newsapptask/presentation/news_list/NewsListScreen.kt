package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import kotlinx.coroutines.delay
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.designsystem.Strings
import com.elgohary.newsapptask.presentation.designsystem.UiDefaults
import com.elgohary.newsapptask.presentation.news_list.components.*

@Composable
fun NewsListScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()
    val connectivityStatus by viewModel.connectivityStatus.collectAsState()

    val pageSize = Constants.DEFAULT_PAGE_SIZE

    var uiState by remember { mutableStateOf(NewsListUiState()) }

    LaunchedEffect(pagingItems.itemCount) {
        val current = pagingItems.itemCount
        if (current > 0 && current % pageSize == 0 && current != uiState.lastBoundaryCount) {
            uiState = uiState.copy(gateActive = true, gateVisibleItemCount = uiState.lastBoundaryCount)
            delay(UiDefaults.GATE_DELAY_MS)
            uiState = uiState.copy(gateActive = false, lastBoundaryCount = current, gateVisibleItemCount = current)
        } else if (uiState.lastBoundaryCount == 0 && current > 0) {
            val baseline = current - (current % pageSize)
            uiState = uiState.copy(lastBoundaryCount = baseline, gateVisibleItemCount = baseline)
        }
    }

    Scaffold { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            if (connectivityStatus != ConnectivityObserver.Status.Available) {
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
                        val visibleCount = uiState.visibleCount(totalCount)
                        if (visibleCount == 0) {
                            EmptyScreen(title = Strings.EmptyNews, message = "")
                        } else {
                            LazyColumn(contentPadding = PaddingValues(bottom = Dimens.ListBottomPadding)) {
                                items(count = visibleCount) { index ->
                                    val article = pagingItems[index] ?: return@items
                                    ArticleCard(article = article, onClick = { onArticleClick(article) })
                                }

                                if (uiState.gateActive) {
                                    item { TimedFooter() }
                                } else {
                                    if (pagingItems.loadState.append is LoadState.Loading) {
                                        item { AppendLoadingIndicator() }
                                    }
                                    if (pagingItems.loadState.append is LoadState.Error) {
                                        val appendError = pagingItems.loadState.append as LoadState.Error
                                        item {
                                            ErrorScreen(
                                                title = Strings.LoadMoreFailed,
                                                message = appendError.error.localizedMessage ?: Strings.LoadMoreFailed,
                                                onRetry = { pagingItems.retry() }
                                            )
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
}
