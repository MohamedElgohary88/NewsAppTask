package com.elgohary.newsapptask.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.elgohary.newsapptask.domain.model.Article

/**
 * Repository abstraction for news domain.
 * - getTopHeadlines: stream paged remote data
 * - bookmarks: local persistence operations
 */
interface NewsRepository {
    fun getTopHeadlines(
        country: String,
        pageSize: Int
    ): Flow<PagingData<Article>>

    // Local bookmarks
    suspend fun upsertBookmark(article: Article)
    suspend fun deleteBookmark(article: Article)
    fun getBookmarks(): Flow<List<Article>>
}