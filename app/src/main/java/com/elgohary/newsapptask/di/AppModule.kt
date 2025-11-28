package com.elgohary.newsapptask.di

import android.content.Context
import com.elgohary.newsapptask.core.Constants
import com.elgohary.newsapptask.data.local.dao.ArticleDao
import com.elgohary.newsapptask.data.local.db.NewsDatabase
import com.elgohary.newsapptask.data.remote.api.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.room.Room
import javax.inject.Singleton
import com.elgohary.newsapptask.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url
            val newUrl = originalUrl.newBuilder().build()
            val newRequest = original.newBuilder()
                .header(Constants.HEADER_API_KEY, BuildConfig.NEWS_API_KEY)
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): NewsApiService =
        retrofit.create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, Constants.DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideArticleDao(db: NewsDatabase): ArticleDao = db.articleDao()
}
