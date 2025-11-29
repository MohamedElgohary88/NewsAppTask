package com.elgohary.newsapptask.presentation.details

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.elgohary.newsapptask.domain.model.Article

@Composable
fun DetailsRoute(
    article: Article,
    onBackClick: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Check bookmark status as soon as we have the article URL
    LaunchedEffect(article.url) {
        viewModel.checkBookmarkStatus(article.url)
    }

    DetailsScreen(
        state = state,
        article = article,
        onEvent = { event ->
            when(event) {
                is DetailsEvent.OnBackClicked -> onBackClick()
                is DetailsEvent.ToggleBookmark -> {
                    viewModel.onEvent(event)
                    Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()
                }
                else -> viewModel.onEvent(event)
            }
        }
    )
}