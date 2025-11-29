package com.elgohary.newsapptask.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.DeleteArticleUseCase
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    selectArticlesUseCase: SelectArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val favoritesFlow: Flow<List<Article>> = selectArticlesUseCase()

    private val _events = MutableSharedFlow<FavoritesEvent>()
    val events = _events.asSharedFlow()

    val state: StateFlow<FavoritesUiState> = favoritesFlow
        .map { list ->
            FavoritesUiState(
                favorites = list,
                isLoading = false,
                isEmpty = list.isEmpty(),
                errorMessage = null
            )
        }
        .onStart { emit(FavoritesUiState(isLoading = true)) }
        .combine(connectivityObserver.observe()) { ui, connectivity ->
            ui.copy(isOffline = connectivity != ConnectivityObserver.Status.Available)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnDeleteClick -> deleteArticle(event.article)
            is FavoritesEvent.OnArticleClick -> viewModelScope.launch { _events.emit(FavoritesEvent.NavigateToArticle(event.article)) }
            FavoritesEvent.OnRetry -> viewModelScope.launch { _events.emit(FavoritesEvent.RetryRequested) }
            else -> Unit
        }
    }

    private fun deleteArticle(article: Article) {
        viewModelScope.launch { deleteArticleUseCase(article) }
    }
}