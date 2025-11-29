package com.elgohary.newsapptask.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsListUiState(
    val isOffline: Boolean = false,
    val gateActive: Boolean = false,
    val errorMsg: String? = null
)

sealed interface NewsListEvent {
    data class OnArticleClick(val article: Article) : NewsListEvent
    object OnRetry : NewsListEvent
}

@HiltViewModel
class NewsListViewModel @Inject constructor(
    getNewsUseCase: GetNewsUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val articles: kotlinx.coroutines.flow.Flow<PagingData<Article>> =
        getNewsUseCase().cachedIn(viewModelScope)

    private val _state = MutableStateFlow(NewsListUiState())
    val state: StateFlow<NewsListUiState> = _state

    private var gateJob: Job? = null

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                _state.update { it.copy(isOffline = status != ConnectivityObserver.Status.Available) }
            }
        }
    }

    fun onItemCountChanged(count: Int) {
        if (count <= 0) return
        val pageSize = Constants.DEFAULT_PAGE_SIZE
        // We only need gateActive now; boundary is implicit.
        if (count % pageSize == 0) {
            startGate()
        }
    }

    private fun startGate() {
        gateJob?.cancel()
        gateJob = viewModelScope.launch {
            _state.update { it.copy(gateActive = true) }
            delay(3000)
            _state.update { it.copy(gateActive = false) }
        }
    }

    fun onEvent(event: NewsListEvent) {
        when (event) {
            is NewsListEvent.OnArticleClick -> { /* navigation handled by caller */ }
            NewsListEvent.OnRetry -> _state.update { it.copy(errorMsg = null) }
        }
    }
}