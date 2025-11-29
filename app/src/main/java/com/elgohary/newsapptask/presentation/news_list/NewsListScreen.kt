package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.designsystem.common.ArticleCard
import com.elgohary.newsapptask.presentation.designsystem.common.EmptyScreen
import com.elgohary.newsapptask.presentation.designsystem.common.ErrorScreen
import com.elgohary.newsapptask.presentation.news_list.components.AppendLoadingIndicator
import com.elgohary.newsapptask.presentation.news_list.components.LoadingListPlaceholder
import com.elgohary.newsapptask.presentation.news_list.components.OfflineBanner
import com.elgohary.newsapptask.presentation.news_list.components.TimedFooter

@Composable
fun NewsListScreen(
    state: NewsListUiState,
    pagingItems: LazyPagingItems<Article>,
    onEvent: (NewsListEvent) -> Unit,
    onArticleClick: (Article) -> Unit
) {
    LaunchedEffect(pagingItems.itemCount) {
        onEvent(NewsListEvent.OnItemCountChanged(pagingItems.itemCount))
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isOffline) {
                OfflineBanner(onRetry = { pagingItems.refresh() })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val refreshState = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> LoadingListPlaceholder()

                    is LoadState.Error -> ErrorScreen(
                        title = stringResource(R.string.unknown_error),
                        message = refreshState.error.localizedMessage ?: stringResource(R.string.unknown_error),
                        onRetry = { pagingItems.retry(); onEvent(NewsListEvent.OnRetry) }
                    )

                    is LoadState.NotLoading -> {
                        if (pagingItems.itemCount == 0) {
                            EmptyScreen(title = stringResource(R.string.empty_news), message = "")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = Dimens.ListBottomPadding)
                            ) {
                                items(count = pagingItems.itemCount) { index ->
                                    val article = pagingItems[index]
                                    if (article != null) {
                                        ArticleCard(
                                            article = article,
                                            onClick = {
                                                onArticleClick(article)
                                            }
                                        )
                                    }
                                }

                                if (state.gateActive) {
                                    item { TimedFooter() }
                                } else {
                                    when (val appendState = pagingItems.loadState.append) {
                                        is LoadState.Loading -> item { AppendLoadingIndicator() }
                                        is LoadState.Error -> item {
                                            ErrorScreen(
                                                title = stringResource(R.string.load_more_failed),
                                                message = appendState.error.localizedMessage ?: stringResource(R.string.load_more_failed),
                                                onRetry = { pagingItems.retry(); onEvent(NewsListEvent.OnRetry) }
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