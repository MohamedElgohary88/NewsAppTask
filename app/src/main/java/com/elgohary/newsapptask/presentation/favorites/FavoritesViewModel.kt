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

data class FavoritesState(
    val favorites: List<Article> = emptyList(),
    val isConnected: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val selectArticlesUseCase: SelectArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)

    val uiState: StateFlow<FavoritesState> = combine(
        selectArticlesUseCase(),
        connectivityObserver.observe(),
        isLoading
    ) { favorites, connectivity, loading ->
        FavoritesState(
            favorites = favorites,
            isConnected = connectivity == ConnectivityObserver.Status.Available,
            isLoading = loading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesState())

    fun deleteArticle(article: Article) {
        viewModelScope.launch { deleteArticleUseCase(article) }
    }
}