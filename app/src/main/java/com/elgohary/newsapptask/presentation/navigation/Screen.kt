package com.elgohary.newsapptask.presentation.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import com.elgohary.newsapptask.domain.model.Article
import kotlinx.serialization.json.Json

private val articleJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector?) {
    data object NewsList : Screen("news_list", "Top Headlines", Icons.AutoMirrored.Filled.Article)
    data object Favorites : Screen("favorites", "Saved", Icons.Filled.Bookmark)

    data object Details : Screen("details/{articleJson}", "Details", null) {
        private const val ARG_KEY = "articleJson"

        // Helper to Create Route with Data
        fun createRoute(article: Article): String {
            val json = articleJson.encodeToString(article)
            val encodedJson = Uri.encode(json) // Encode to handle special chars like '/'
            return "details/$encodedJson"
        }

        // Helper to Extract Data safely
        fun getArticle(entry: NavBackStackEntry): Article? {
            val encodedJson = entry.arguments?.getString(ARG_KEY) ?: return null
            return try {
                val decodedJson = Uri.decode(encodedJson)
                articleJson.decodeFromString<Article>(decodedJson)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}