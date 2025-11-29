package com.elgohary.newsapptask.presentation.favorites

import com.elgohary.newsapptask.domain.model.Article

data class FavoritesUiState(
    val favorites: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null
)