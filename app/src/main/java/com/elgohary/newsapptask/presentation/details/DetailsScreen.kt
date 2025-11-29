package com.elgohary.newsapptask.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.designsystem.Dimens
import com.elgohary.newsapptask.presentation.details.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsUiState,
    article: Article,
    onEvent: (DetailsEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DetailsTopBar(onBack = { onEvent(DetailsEvent.OnBackClicked) })
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !state.isBookmarked,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SaveFab(
                    onClick = { onEvent(DetailsEvent.ToggleBookmark(article)) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ArticleHeaderImage(
                imageUrl = article.urlToImage,
                title = article.title
            )

            Column(modifier = Modifier.padding(Dimens.SpacingLG)) {
                ArticleTitleAndMeta(
                    title = article.title.orEmpty(),
                    publishedAt = article.publishedAt.orEmpty(),
                    sourceName = article.source?.name
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingLG))

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