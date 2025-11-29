package com.elgohary.newsapptask.domain.usecase

import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SelectArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.getBookmarks()
}

