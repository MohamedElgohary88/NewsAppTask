package com.elgohary.newsapptask.domain.usecase

import androidx.paging.PagingData
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        country: String = Constants.DEFAULT_COUNTRY,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Flow<PagingData<Article>> = repository.getTopHeadlines(country = country, pageSize = pageSize)
}