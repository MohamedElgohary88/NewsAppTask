package com.elgohary.newsapptask.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.DeleteArticleUseCase
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null
)

sealed interface FavoritesEvent {
    data class OnArticleClick(val article: Article) : FavoritesEvent
    data class OnDeleteClick(val article: Article) : FavoritesEvent
    object OnRetry : FavoritesEvent
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val selectArticlesUseCase: SelectArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState

    private val connectivityFlow = connectivityObserver.observe().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ConnectivityObserver.Status.Unavailable
    )

    init {
        observeFavorites()
        observeConnectivity()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            selectArticlesUseCase().collectLatest { list ->
                _uiState.update {
                    it.copy(
                        favorites = list,
                        isEmpty = list.isEmpty(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityFlow.collectLatest { status ->
                _uiState.update {
                    it.copy(isOffline = status != ConnectivityObserver.Status.Available)
                }
            }
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnDeleteClick -> deleteArticle(event.article)
            is FavoritesEvent.OnArticleClick -> { /* navigation handled by caller */ }
            FavoritesEvent.OnRetry -> refresh()
        }
    }

    private fun refresh() {
        // No explicit refresh source here (local DB is always observed),
        // but we clear any error and could re-trigger observation if needed.
        _uiState.update { it.copy(errorMessage = null, isLoading = false) }
    }

    private fun deleteArticle(article: Article) {
        viewModelScope.launch {
            deleteArticleUseCase(article)
        }
    }
}