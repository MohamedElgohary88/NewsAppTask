package com.elgohary.newsapptask.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import com.elgohary.newsapptask.domain.usecase.UpsertArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsState(
    val isBookmarked: Boolean = false,
    val isOffline: Boolean = false
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val upsertArticleUseCase: UpsertArticleUseCase,
    selectArticlesUseCase: SelectArticlesUseCase,
    connectivityObserver: ConnectivityObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Assume navigation passed "articleUrl"; fallback to empty
    private val articleUrl: String = savedStateHandle.get<String>("articleUrl") ?: ""

    private val bookmarkedFlow = selectArticlesUseCase().map { list ->
        articleUrl.isNotEmpty() && list.any { it.url == articleUrl }
    }

    val uiState: StateFlow<DetailsState> = combine(
        bookmarkedFlow,
        connectivityObserver.observe()
    ) { bookmarked, status ->
        DetailsState(
            isBookmarked = bookmarked,
            isOffline = status != ConnectivityObserver.Status.Available
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsState())

    fun saveArticle(article: Article) {
        viewModelScope.launch { upsertArticleUseCase(article) }
    }
}