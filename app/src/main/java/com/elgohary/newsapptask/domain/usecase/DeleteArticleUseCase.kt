package com.elgohary.newsapptask.domain.usecase

import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.repository.NewsRepository
import javax.inject.Inject

class DeleteArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: Article) {
        repository.deleteBookmark(article)
    }
}