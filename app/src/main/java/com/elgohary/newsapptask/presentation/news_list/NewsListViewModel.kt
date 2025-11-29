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
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    // Paging stream stays separate (collected as LazyPagingItems in UI) per instructions
    val articles: Flow<PagingData<Article>> = getNewsUseCase().cachedIn(viewModelScope)

    // Internal mutable state for gating logic
    private val gateActive = MutableStateFlow(false)
    private val pageBoundaryCount = MutableStateFlow(0)
    private var gateJob: Job? = null

    // Exposed unified UI state
    val uiState: StateFlow<NewsListState> = combine(
        connectivityObserver.observe(),
        gateActive,
        _error
    ) { connectivity, gate, error ->
        NewsListState(
            isConnected = connectivity == ConnectivityObserver.Status.Available,
            gateActive = gate,
            errorMsg = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsListState())

    // Error placeholder (could be set when refresh loadState emits error via an external listener)
    private val _error = MutableStateFlow<String?>(null)

    /** Called by UI when current itemCount changes (pagingItems.itemCount) */
    fun onItemCountChanged(count: Int) {
        if (count <= 0) return
        val pageSize = Constants.DEFAULT_PAGE_SIZE
        // Trigger gate only when crossing an exact multiple boundary not seen before
        if (count % pageSize == 0 && count != pageBoundaryCount.value) {
            pageBoundaryCount.value = count
            startGate(count)
        }
    }

    private fun startGate(latestCount: Int) {
        gateJob?.cancel()
        gateActive.value = true
        gateJob = viewModelScope.launch {
            delay(3000) // matches UiDefaults.GATE_DELAY_MS without UI layer dependency
            gateActive.value = false
        }
    }

    /** Optional: allow setting an error from outside (e.g., paging listener) */
    fun setError(message: String?) { _error.value = message }
}