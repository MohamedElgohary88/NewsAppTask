package com.elgohary.newsapptask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elgohary.newsapptask.data.local.dao.ArticleDao
import com.elgohary.newsapptask.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

