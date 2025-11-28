package com.elgohary.newsapptask.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.data.mapper.toDomain
import com.elgohary.newsapptask.data.remote.api.NewsApiService
import com.elgohary.newsapptask.domain.model.Article
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val apiService: NewsApiService,
    private val country: String,
    private val pageSize: Int
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: Constants.FIRST_PAGE_INDEX
        return try {
            val response = apiService.getTopHeadlines(
                country = country,
                page = page,
                pageSize = pageSize
            )
            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }
            val body = response.body()
            val dtos = body?.articles.orEmpty()
            val mapped = dtos
                .map { it.toDomain() }
                .filter { it.title != "[Removed]" }
            val nextKey = if (mapped.isEmpty()) null else page + 1
            val prevKey = if (page == Constants.FIRST_PAGE_INDEX) null else page - 1
            LoadResult.Page(
                data = mapped,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}

