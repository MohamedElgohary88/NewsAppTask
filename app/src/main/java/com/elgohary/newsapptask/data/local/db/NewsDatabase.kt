package com.elgohary.newsapptask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elgohary.newsapptask.data.local.Converters
import com.elgohary.newsapptask.data.local.dao.ArticleDao
import com.elgohary.newsapptask.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

