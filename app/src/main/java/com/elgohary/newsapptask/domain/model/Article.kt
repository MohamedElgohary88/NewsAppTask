package com.elgohary.newsapptask.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a news article used across the app (UI, domain, data mappers).
 * All fields are nullable to reflect external data variability and avoid domain coupling to
 * remote schema constraints.
 */
@Serializable
data class Article(
    val source: Source?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)
