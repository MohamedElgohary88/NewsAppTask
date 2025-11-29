package com.elgohary.newsapptask.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "articles",
    indices = [Index(value = ["url"], unique = true)]
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sourceId: String?,
    val sourceName: String?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)