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
    private val selectArticlesUseCase: SelectArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    // Internal State
    private val _favoritesFlow = MutableStateFlow<List<Article>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    // Combined UI State
    val uiState: StateFlow<FavoritesUiState> = combine(
        _favoritesFlow,
        _isLoading,
        _error,
        connectivityObserver.observe()
    ) { favorites, loading, error, connectivity ->
        FavoritesUiState(
            favorites = favorites,
            isLoading = loading,
            isEmpty = favorites.isEmpty() && !loading,
            errorMessage = error,
            isOffline = connectivity != ConnectivityObserver.Status.Available
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoritesUiState())

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                selectArticlesUseCase().collectLatest { list ->
                    _favoritesFlow.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnDeleteClick -> deleteArticle(event.article)
            is FavoritesEvent.OnArticleClick -> { /* Navigation handled by Route */ }
            FavoritesEvent.OnRetry -> { observeFavorites() }
        }
    }

    private fun deleteArticle(article: Article) {
        viewModelScope.launch {
            deleteArticleUseCase(article)
        }
    }
}