package com.elgohary.newsapptask.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.elgohary.newsapptask.data.local.dao.ArticleDao
import com.elgohary.newsapptask.data.paging.NewsPagingSource
import com.elgohary.newsapptask.data.remote.api.NewsApiService
import com.elgohary.newsapptask.data.mapper.toEntity
import com.elgohary.newsapptask.data.mapper.toDomain
import com.elgohary.newsapptask.domain.model.Article
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
        articleDao.upsert(article.toEntity())
    }

    override suspend fun deleteBookmark(article: Article) {
        val url = article.url ?: return
        articleDao.deleteByUrl(url)
    }

    override fun getBookmarks(): Flow<List<Article>> {
        return articleDao.getArticles().map { list ->
            list.map { entity -> entity.toDomain() }
        }
    }
}