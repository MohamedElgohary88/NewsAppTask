package com.elgohary.newsapptask.presentation.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.elgohary.newsapptask.presentation.details.DetailsScreen
import com.elgohary.newsapptask.presentation.favorites.FavoritesScreen
import com.elgohary.newsapptask.presentation.news_list.NewsListScreen
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

// Configure Json once
private val articleJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object NewsList : Screen("news_list", "Top Headlines", Icons.AutoMirrored.Filled.Article)
    object Favorites : Screen("favorites", "Saved", Icons.Filled.Bookmark)
    object Details : Screen("details/{articleJson}", "Details", null) {
        fun createRoute(article: com.elgohary.newsapptask.domain.model.Article): String {
            val jsonEncoded = Uri.encode(articleJson.encodeToString(article))
            return "details/$jsonEncoded"
        }
    }
}

@Composable
fun NewsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(Screen.NewsList, Screen.Favorites)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute == Screen.NewsList.route || currentRoute == Screen.Favorites.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.NewsList.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.label) } },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.NewsList.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.NewsList.route) {
                NewsListScreen(onArticleClick = { article ->
                    navController.navigate(Screen.Details.createRoute(article))
                })
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onArticleClick = { article ->
                    navController.navigate(Screen.Details.createRoute(article))
                })
            }
            composable(Screen.Details.route) { backStackEntry ->
                val encodedJson = backStackEntry.arguments?.getString("articleJson")
                if (encodedJson == null) {
                    navController.popBackStack(); return@composable
                }
                val decoded = Uri.decode(encodedJson)
                val article = try {
                    articleJson.decodeFromString<com.elgohary.newsapptask.domain.model.Article>(decoded)
                } catch (e: SerializationException) {
                    navController.popBackStack(); return@composable
                }
                DetailsScreen(article = article, navController = navController)
            }
        }
    }
}
