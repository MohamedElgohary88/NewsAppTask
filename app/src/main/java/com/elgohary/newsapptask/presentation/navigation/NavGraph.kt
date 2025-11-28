package com.elgohary.newsapptask.presentation.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.details.DetailsScreen
import com.elgohary.newsapptask.presentation.favorites.FavoritesScreen
import com.elgohary.newsapptask.presentation.news_list.NewsListScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object NewsList : Screen("news_list", "Top Headlines", Icons.Filled.Article)
    object Favorites : Screen("favorites", "Saved", Icons.Filled.Bookmark)
    object Details : Screen("details/{articleJson}", "Details", null) {
        fun createRoute(article: com.elgohary.newsapptask.domain.model.Article): String {
            val json = Json.encodeToString(article)
            val encoded = Uri.encode(json)
            return "details/$encoded"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
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
                            icon = {
                                screen.icon?.let { Icon(it, contentDescription = screen.label) }
                            },
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
            modifier = modifier
        ) {
            composable(Screen.NewsList.route) {
                NewsListScreen(
                    onArticleClick = { article ->
                        navController.navigate(Screen.Details.createRoute(article))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onArticleClick = { article ->
                        navController.navigate(Screen.Details.createRoute(article))
                    }
                )
            }

            composable(Screen.Details.route) { backStackEntry ->
                val encodedJson = backStackEntry.arguments?.getString("articleJson")
                val json = encodedJson?.let { Uri.decode(it) }
                val article = if (!json.isNullOrEmpty()) Json.decodeFromString<com.elgohary.newsapptask.domain.model.Article>(json) else com.elgohary.newsapptask.domain.model.Article(null, null, null, null, null, null, null, null)
                DetailsScreen(article = article, navController = navController)
            }
        }
    }
}
