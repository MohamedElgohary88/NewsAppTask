package com.elgohary.newsapptask.presentation.news_list

/** Unified UI state for NewsList screen following MVVM & UDF. */
data class NewsListState(
    val isConnected: Boolean = true,
    val gateActive: Boolean = false,
    val errorMsg: String? = null
)
