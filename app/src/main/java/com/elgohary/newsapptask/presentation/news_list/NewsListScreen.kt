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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import com.elgohary.newsapptask.presentation.common.ShimmerEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onArticleClick: (com.elgohary.newsapptask.domain.model.Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    var previousCount by remember { mutableStateOf(0) }
    var showGate by remember { mutableStateOf(false) }
    var gateBaseCount by remember { mutableStateOf(0) }

    // Trigger gate when new page appended (count increases past a multiple of page size)
    LaunchedEffect(pagingItems.itemCount) {
        val current = pagingItems.itemCount
        val pageSize = Constants.DEFAULT_PAGE_SIZE
        // Only trigger after first page fully loaded (previousCount >= pageSize) and on an increase
        if (current > previousCount && previousCount >= pageSize && previousCount % pageSize == 0) {
            // Start gate: hide newly appended items for 3 seconds
            showGate = true
            gateBaseCount = previousCount // show only items up to previous boundary
            delay(3000L)
            showGate = false
            previousCount = current
        } else if (previousCount == 0 && current > 0) {
            // Initialize previous count after first load completes
            previousCount = current
        }
    }

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
                    val totalCount = pagingItems.itemCount
                    val displayedCount = if (showGate) gateBaseCount else totalCount
                    if (displayedCount == 0) {
                        EmptyScreen(message = "No news available")
                    } else {
                        LazyColumn {
                            items(count = displayedCount) { index ->
                                val article = pagingItems[index]
                                if (article != null) {
                                    ArticleCard(
                                        article = article,
                                        onClick = { onArticleClick(article) }
                                    )
                                }
                            }

                            // Timed footer during gate
                            if (showGate) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            } else {
                                // Normal append loading indicator
                                item {
                                    if (pagingItems.loadState.append is LoadState.Loading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
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
}
