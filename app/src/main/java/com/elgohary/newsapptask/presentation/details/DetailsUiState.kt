package com.elgohary.newsapptask.presentation.details

data class DetailsUiState(
    val isBookmarked: Boolean = false,
    val isOffline: Boolean = false,
    val isDescriptionExpanded: Boolean = false
)