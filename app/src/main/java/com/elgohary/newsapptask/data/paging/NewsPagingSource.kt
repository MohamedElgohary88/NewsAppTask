package com.elgohary.newsapptask.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.data.mapper.toDomain
import com.elgohary.newsapptask.data.remote.api.NewsApiService
import com.elgohary.newsapptask.data.util.executeSafely
import com.elgohary.newsapptask.domain.model.Article
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource for News API with robust mapping, safe API call wrapper, and
 * improved nextKey computation using totalResults when available.
 */
class NewsPagingSource(
    private val apiService: NewsApiService,
    private val country: String,
    private val defaultPageSize: Int
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            state.closestPageToPosition(anchorPos)?.let { closest ->
                closest.prevKey?.plus(1) ?: closest.nextKey?.minus(1)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: Constants.FIRST_PAGE_INDEX
        val requestedPageSize = params.loadSize.coerceAtMost(defaultPageSize)

        val responseResult = executeSafely {
            apiService.getTopHeadlines(
                country = country,
                page = page,
                pageSize = requestedPageSize
            )
        }

        if (responseResult.isFailure) {
            return LoadResult.Error(responseResult.exceptionOrNull() ?: IOException("Unknown network error"))
        }

        val response = responseResult.getOrNull()!!
        if (!response.isSuccessful) {
            return LoadResult.Error(HttpException(response))
        }

        val body = response.body() ?: return LoadResult.Error(NullPointerException("Response body is null"))
        val dTos = body.articles.orEmpty()
        val items = dTos.mapNotNull { dto ->
            try {
                dto.toDomain()
            } catch (_: Throwable) {
                null
            }
        }

        val filtered = items.filter { !it.title.isNullOrBlank() && it.title != "[Removed]" }
        val deduped = filtered.distinctBy { it.url }
        val nextKey = when {
            body.totalResults != null -> {
                val total = body.totalResults
                val lastPage = if (requestedPageSize > 0) (total + requestedPageSize - 1) / requestedPageSize else null
                if (lastPage == null) page + 1 else if (page >= lastPage) null else page + 1
            }
            dTos.size < requestedPageSize -> null
            else -> page + 1
        }

        val prevKey = if (page == Constants.FIRST_PAGE_INDEX) null else page - 1

        return LoadResult.Page(
            data = deduped,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}