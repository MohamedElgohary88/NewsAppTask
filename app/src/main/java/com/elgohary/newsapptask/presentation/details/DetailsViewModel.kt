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

data class DetailsUiState(
    val isBookmarked: Boolean = false,
    val isOffline: Boolean = false,
    val isDescriptionExpanded: Boolean = false
)

sealed interface DetailsEvent {
    object ToggleDescription : DetailsEvent
    data class ToggleBookmark(val article: Article) : DetailsEvent
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val upsertArticleUseCase: UpsertArticleUseCase,
    selectArticlesUseCase: SelectArticlesUseCase,
    connectivityObserver: ConnectivityObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsUiState())
    val state: StateFlow<DetailsUiState> = _state

    private val articleUrl: String = savedStateHandle.get<String>("articleUrl") ?: ""

    private val connectivityFlow = connectivityObserver.observe().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ConnectivityObserver.Status.Unavailable
    )

    init {
        observeBookmark(selectArticlesUseCase)
        observeConnectivity()
    }

    private fun observeBookmark(selectArticlesUseCase: SelectArticlesUseCase) {
        viewModelScope.launch {
            selectArticlesUseCase().collectLatest { list ->
                val bookmarked = articleUrl.isNotEmpty() && list.any { it.url == articleUrl }
                _state.update { it.copy(isBookmarked = bookmarked) }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityFlow.collectLatest { status ->
                _state.update { it.copy(isOffline = status != ConnectivityObserver.Status.Available) }
            }
        }
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.ToggleBookmark -> toggleBookmark(event.article)
            DetailsEvent.ToggleDescription -> toggleDescriptionExpansion()
        }
    }

    private fun toggleDescriptionExpansion() {
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