package com.elgohary.newsapptask.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import com.elgohary.newsapptask.domain.usecase.UpsertArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailsUiEvent {

}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val upsertArticleUseCase: UpsertArticleUseCase,
    private val selectArticlesUseCase: SelectArticlesUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val currentArticleUrl = MutableStateFlow<String?>(null)
    private val _isDescriptionExpanded = MutableStateFlow(false)
    private val _events = MutableSharedFlow<DetailsUiEvent>()
    val events = _events.asSharedFlow()

    private val isBookmarkedFlow: Flow<Boolean> = currentArticleUrl
        .filterNotNull()
        .flatMapLatest { url ->
            selectArticlesUseCase().map { saved -> saved.any { it.url == url } }
        }

    val state: StateFlow<DetailsUiState> = combine(
        isBookmarkedFlow.onStart { emit(false) },
        _isDescriptionExpanded,
        connectivityObserver.observe()
    ) { isBookmarked, descExpanded, connectivity ->
        DetailsUiState(
            isBookmarked = isBookmarked,
            isOffline = connectivity != ConnectivityObserver.Status.Available,
            isDescriptionExpanded = descExpanded
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsUiState())

    fun startObservingArticle(url: String?) {
        currentArticleUrl.value = url
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.ToggleBookmark -> toggleBookmark(event.article)
            DetailsEvent.ToggleDescription -> toggleDescription()
            DetailsEvent.OnBackClicked -> viewModelScope.launch {  }
            else -> {

            }
        }
    }

    private fun toggleDescription() {
        _isDescriptionExpanded.update { !it }
    }

    private fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (!state.value.isBookmarked) upsertArticleUseCase(article)
        }
    }
}