package com.elgohary.newsapptask.presentation.news_list

import com.elgohary.newsapptask.domain.model.Article

sealed interface NewsListEvent {
    data class OnArticleClick(val article: Article) : NewsListEvent
    data class OnItemCountChanged(val count: Int) : NewsListEvent
    object OnRetry : NewsListEvent
}