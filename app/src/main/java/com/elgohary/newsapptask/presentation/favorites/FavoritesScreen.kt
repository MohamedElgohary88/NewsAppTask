package com.elgohary.newsapptask.presentation.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.elgohary.newsapptask.presentation.common.ArticleCard
import com.elgohary.newsapptask.presentation.common.EmptyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onArticleClick: (String) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (favorites.isEmpty()) {
                EmptyScreen(message = "No favorites yet")
            } else {
                LazyColumn {
                    items(favorites) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article.url ?: "") }
                        )
                    }
                }
            }
        }
    }
}
