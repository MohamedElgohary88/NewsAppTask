package com.elgohary.newsapptask.domain.model

import kotlinx.serialization.Serializable

/** Describes the origin of an Article. */
@Serializable
data class Source(
    val id: String?,
    val name: String?
)
