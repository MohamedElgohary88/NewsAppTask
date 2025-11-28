package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import com.elgohary.newsapptask.presentation.common.ShimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onArticleClick: (com.elgohary.newsapptask.domain.model.Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = pagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    ShimmerEffect(modifier = Modifier.fillMaxSize())
                }

                is LoadState.Error -> {
                    ErrorScreen(
                        message = state.error.localizedMessage ?: "Unknown error",
                        onRetry = { pagingItems.retry() }
                    )
                }

                is LoadState.NotLoading -> {
                    if (pagingItems.itemCount == 0) {
                        EmptyScreen(message = "No news available")
                    } else {
                        LazyColumn {
                            items(count = pagingItems.itemCount) { index ->
                                val article = pagingItems[index]
                                if (article != null) {
                                    ArticleCard(
                                        article = article,
                                        onClick = { onArticleClick(article) }
                                    )
                                }

                                // Footer after every 20 items (visible even when total == 20)
                                if ((index + 1) % 20 == 0) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .alpha(0.8f)
                                        )
                                    }
                                }
                            }

                            // Default paging append footer
                            item {
                                if (pagingItems.loadState.append is LoadState.Loading) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
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
