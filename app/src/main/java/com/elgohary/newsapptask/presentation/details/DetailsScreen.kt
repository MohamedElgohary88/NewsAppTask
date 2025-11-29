package com.elgohary.newsapptask.presentation.details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.details.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRoute(
    article: Article,
    navController: NavHostController? = null,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    DetailsScreen(
        state = state,
        article = article,
        onEvent = viewModel::onEvent,
        onBack = { navController?.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsUiState,
    article: Article,
    onEvent: (DetailsEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { DetailsTopBar(onBack = onBack) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !state.isBookmarked,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SaveFab(onClick = {
                    onEvent(DetailsEvent.ToggleBookmark(article))
                    Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()
                })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ArticleHeaderImage(imageUrl = article.urlToImage, title = article.title)

            Column(modifier = Modifier.padding(16.dp)) {
                ArticleTitleAndMeta(
                    title = article.title.orEmpty(),
                    publishedAt = article.publishedAt.orEmpty(),
                    sourceName = article.source?.name
                )

                Spacer(modifier = Modifier.height(16.dp))

                ArticleDescription(
                    description = article.description,
                    content = article.content,
                    isExpanded = state.isDescriptionExpanded,
                    onToggleExpand = { onEvent(DetailsEvent.ToggleDescription) },
                    articleUrl = article.url
                )
            }
        }
    }
}