package com.elgohary.newsapptask.data.mapper

import com.elgohary.newsapptask.data.remote.dto.ArticleDto
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.model.Source

fun ArticleDto.toDomain(): Article = Article(
    source = this.source?.let { Source(id = it.id, name = it.name) },
    author = this.author,
    title = this.title,
    description = this.description,
    url = this.url,
    urlToImage = this.urlToImage,
    publishedAt = this.publishedAt,
    content = this.content
)

