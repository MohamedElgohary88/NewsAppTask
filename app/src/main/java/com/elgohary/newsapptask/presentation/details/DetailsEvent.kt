package com.elgohary.newsapptask.presentation.details

import com.elgohary.newsapptask.domain.model.Article

sealed interface DetailsEvent {
    object ToggleDescription : DetailsEvent
    data class ToggleBookmark(val article: Article) : DetailsEvent
    object OnBackClicked : DetailsEvent

    data class ShowMessage(val message: String) : DetailsEvent
}