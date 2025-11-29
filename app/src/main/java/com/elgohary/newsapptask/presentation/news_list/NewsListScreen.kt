package com.elgohary.newsapptask.presentation.news_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen
import com.elgohary.newsapptask.presentation.common.ErrorScreen
import com.elgohary.newsapptask.presentation.common.ShimmerEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.articles.collectAsLazyPagingItems()
    val connectivityStatus by viewModel.connectivityStatus.collectAsState()

    // Gate state: show a 3s footer after every multiple-of-pageSize boundary crossed
    val pageSize = 20 // fallback if server uses 20 per page
    var lastBoundaryCount by remember { mutableIntStateOf(0) }
    var gateActive by remember { mutableStateOf(false) }
    var gateVisibleItemCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagingItems.itemCount) {
        val current = pagingItems.itemCount
        // Trigger only when we cross a new full page boundary (exclude first boundary where lastBoundaryCount==0)
        if (current > 0 && current % pageSize == 0 && current != lastBoundaryCount) {
            gateActive = true
            gateVisibleItemCount = lastBoundaryCount
            delay(3000L)
            gateActive = false
        } else if (lastBoundaryCount == 0 && current > 0) {
            // Initialize after first batch
            lastBoundaryCount = current - (current % pageSize) // nearest boundary <= current
            gateVisibleItemCount = lastBoundaryCount
        }
    }

    Scaffold { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            if (connectivityStatus != ConnectivityObserver.Status.Available) {
                Surface(color = Color.Red.copy(alpha = 0.9f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "No Internet Connection", color = Color.White)
                        Text(
                            text = "Retry",
                            color = Color.White,
                            modifier = Modifier.clickable { pagingItems.refresh() }
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            repeat(6) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) { ShimmerEffect(modifier = Modifier.fillMaxSize()) }
                            }
                        }
                    }
                    is LoadState.Error -> {
                        ErrorScreen(
                            message = state.error.localizedMessage ?: "Unknown error",
                            onRetry = { pagingItems.retry() }
                        )
                    }
                    is LoadState.NotLoading -> {
                        val totalCount = pagingItems.itemCount
                        // If gate active, restrict visible items to gateVisibleItemCount; else show all
                        val visibleCount = if (gateActive) gateVisibleItemCount else totalCount
                        if (visibleCount == 0) {
                            EmptyScreen(message = "No news available")
                        } else {
                            LazyColumn(contentPadding = PaddingValues(bottom = 12.dp)) {
                                // Use paging compose items extension for cleaner null handling
                                items(count = visibleCount) { index ->
                                    val article = pagingItems[index] ?: return@items
                                    ArticleCard(article = article, onClick = { onArticleClick(article) })
                                }

                                // Footer while gateActive (timed)
                                if (gateActive) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                                    }
                                } else {
                                    // Normal append load state indicator
                                    if (pagingItems.loadState.append is LoadState.Loading) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) { CircularProgressIndicator() }
                                        }
                                    }
                                    if (pagingItems.loadState.append is LoadState.Error) {
                                        val appendError = pagingItems.loadState.append as LoadState.Error
                                        item {
                                            ErrorScreen(
                                                message = appendError.error.localizedMessage ?: "Load more failed",
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
