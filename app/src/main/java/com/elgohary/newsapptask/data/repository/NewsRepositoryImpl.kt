package com.elgohary.newsapptask.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.elgohary.newsapptask.data.local.dao.ArticleDao
import com.elgohary.newsapptask.data.local.entity.ArticleEntity
import com.elgohary.newsapptask.data.paging.NewsPagingSource
import com.elgohary.newsapptask.data.remote.api.NewsApiService
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.model.Source
import com.elgohary.newsapptask.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {
    override fun getTopHeadlines(country: String, pageSize: Int): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { NewsPagingSource(apiService, country, pageSize) }
        ).flow
    }

    override suspend fun upsertBookmark(article: Article) {
        val entity = ArticleEntity(
            sourceId = article.source?.id,
            sourceName = article.source?.name,
            author = article.author,
            title = article.title,
            description = article.description,
            url = article.url,
            urlToImage = article.urlToImage,
            publishedAt = article.publishedAt,
            content = article.content
        )
        articleDao.upsert(entity)
    }

    override suspend fun deleteBookmark(article: Article) {
        // Use URL as natural key to delete, avoiding primary-key mismatch with auto-generated id
        val url = article.url ?: return
        articleDao.deleteByUrl(url)
    }

    override fun getBookmarks(): Flow<List<Article>> {
        return articleDao.getArticles().map { list ->
            list.map { entity ->
                Article(
                    source = Source(entity.sourceId, entity.sourceName),
                    author = entity.author,
                    title = entity.title,
                    description = entity.description,
                    url = entity.url,
                    urlToImage = entity.urlToImage,
                    publishedAt = entity.publishedAt,
                    content = entity.content
                )
            }
        }
    }
}