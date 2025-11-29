package com.elgohary.newsapptask.presentation.favorites

import com.elgohary.newsapptask.domain.model.Article

sealed interface FavoritesEvent {
    data class OnArticleClick(val article: Article) : FavoritesEvent
    data class OnDeleteClick(val article: Article) : FavoritesEvent
    object OnRetry : FavoritesEvent
    // One-off UI events for ViewModel to emit
    data class NavigateToArticle(val article: Article) : FavoritesEvent
    object RetryRequested : FavoritesEvent
    data class ShowMessage(val message: String) : FavoritesEvent
}