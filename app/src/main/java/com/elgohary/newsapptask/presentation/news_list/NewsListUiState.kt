package com.elgohary.newsapptask.presentation.news_list

/** Rich UI state for NewsList screen with gate logic fields. */
data class NewsListUiState(
    val isOffline: Boolean = false,
    val gateActive: Boolean = false,
    val errorMsg: String? = null
)
