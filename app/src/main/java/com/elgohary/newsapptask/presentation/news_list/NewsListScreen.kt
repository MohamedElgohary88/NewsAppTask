package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onArticleClick: (String) -> Unit,
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
                            items(pagingItems.itemCount) { index ->
                                val article = pagingItems[index]
                                if (article != null) {
                                    ArticleCard(
                                        article = article,
                                        onClick = { onArticleClick(article.url ?: "") }
                                    )
                                }
                            }

                            item {
                                if (pagingItems.loadState.append is LoadState.Loading) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
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
