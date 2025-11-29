package com.elgohary.newsapptask.data.mapper

import com.elgohary.newsapptask.data.remote.dto.ArticleDto
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.model.Source
import com.elgohary.newsapptask.data.local.entity.ArticleEntity

// Map Dto -> Domain
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

// Map Domain -> Entity
fun Article.toEntity(): ArticleEntity = ArticleEntity(
    sourceId = this.source?.id,
    sourceName = this.source?.name,
    author = this.author,
    title = this.title,
    description = this.description,
    url = this.url,
    urlToImage = this.urlToImage,
    publishedAt = this.publishedAt,
    content = this.content
)

// Map Entity -> Domain
fun ArticleEntity.toDomain(): Article = Article(
    source = Source(this.sourceId, this.sourceName),
    author = this.author,
    title = this.title,
    description = this.description,
    url = this.url,
    urlToImage = this.urlToImage,
    publishedAt = this.publishedAt,
    content = this.content
)
