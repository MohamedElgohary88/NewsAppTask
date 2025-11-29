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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.details.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    article: Article,
    navController: NavHostController? = null,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val savedUrls by viewModel.savedUrls.collectAsState()
    val alreadySaved = article.url?.let { savedUrls.contains(it) } == true
    val expandedState = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DetailsTopBar(onBack = { navController?.popBackStack() })
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !alreadySaved,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SaveFab(onClick = {
                    viewModel.saveArticle(article)
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
                    isExpanded = expandedState.value,
                    onToggleExpand = { expandedState.value = !expandedState.value },
                    articleUrl = article.url
                )
            }
        }
    }
}