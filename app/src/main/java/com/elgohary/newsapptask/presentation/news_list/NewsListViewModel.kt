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

@HiltViewModel
class NewsListViewModel @Inject constructor(
    getNewsUseCase: GetNewsUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val articles: Flow<PagingData<Article>> =
        getNewsUseCase().cachedIn(viewModelScope)

    private val _state = MutableStateFlow(NewsListUiState())
    val state: StateFlow<NewsListUiState> = _state

    private var gateJob: Job? = null
    private var lastBoundaryCount = 0

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                _state.update {
                    it.copy(isOffline = status != ConnectivityObserver.Status.Available)
                }
            }
        }
    }

    fun onEvent(event: NewsListEvent) {
        when (event) {
            is NewsListEvent.OnArticleClick -> { /* Navigation handled by Route/Caller */ }
            NewsListEvent.OnRetry -> _state.update { it.copy(errorMsg = null) }
            is NewsListEvent.OnItemCountChanged -> handleGateLogic(event.count)
        }
    }

    private fun handleGateLogic(count: Int) {
        if (count <= 0) return
        val pageSize = Constants.DEFAULT_PAGE_SIZE


        if (count % pageSize == 0 && count != lastBoundaryCount) {
            lastBoundaryCount = count
            triggerGate()
        }
    }

    private fun triggerGate() {
        gateJob?.cancel()
        gateJob = viewModelScope.launch {
            _state.update { it.copy(gateActive = true) }
            delay(3000)
            _state.update { it.copy(gateActive = false) }
        }
    }
}