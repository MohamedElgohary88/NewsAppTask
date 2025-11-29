package com.elgohary.newsapptask.presentation.details

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

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val upsertArticleUseCase: UpsertArticleUseCase,
    private val selectArticlesUseCase: SelectArticlesUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsUiState())

    val state: StateFlow<DetailsUiState> = combine(
        _state,
        connectivityObserver.observe()
    ) { currentState, connectivityStatus ->
        currentState.copy(
            isOffline = connectivityStatus != ConnectivityObserver.Status.Available
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsUiState())

    fun checkBookmarkStatus(articleUrl: String?) {
        if (articleUrl.isNullOrEmpty()) return

        viewModelScope.launch {
            selectArticlesUseCase().collect { savedArticles ->
                val isSaved = savedArticles.any { it.url == articleUrl }
                _state.update { it.copy(isBookmarked = isSaved) }
            }
        }
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.ToggleBookmark -> toggleBookmark(event.article)
            DetailsEvent.ToggleDescription -> toggleDescription()
            DetailsEvent.OnBackClicked -> { /* Handled by UI callback */ }
        }
    }

    private fun toggleDescription() {
        _state.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }

    private fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (!_state.value.isBookmarked) {
                upsertArticleUseCase(article)
            }
        }
    }
}