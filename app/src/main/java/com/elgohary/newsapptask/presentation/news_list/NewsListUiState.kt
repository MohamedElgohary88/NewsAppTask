package com.elgohary.newsapptask.presentation.news_list

/**
 * UI state holder for News list screen. Keeps ephemeral UI-only flags together.
 */
data class NewsListUiState(
    val gateActive: Boolean = false,
    val lastBoundaryCount: Int = 0,
    val gateVisibleItemCount: Int = 0
) {
    fun visibleCount(totalCount: Int): Int = if (gateActive) gateVisibleItemCount else totalCount
}
