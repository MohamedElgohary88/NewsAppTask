package com.elgohary.newsapptask.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elgohary.newsapptask.presentation.details.DetailsRoute
import com.elgohary.newsapptask.presentation.favorites.FavoritesRoute
import com.elgohary.newsapptask.presentation.news_list.NewsListRoute

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
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NewsBottomNavigation(
                    items = items,
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.NewsList.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.NewsList.route,
            modifier = modifier.padding(innerPadding)
        ) {
            // --- News List Destination ---
            composable(Screen.NewsList.route) {
                NewsListRoute(
                    onArticleClick = { article ->
                        navController.navigate(Screen.Details.createRoute(article))
                    }
                )
            }

            // --- Favorites Destination ---
            composable(Screen.Favorites.route) {
                FavoritesRoute(
                    onArticleClick = { article ->
                        navController.navigate(Screen.Details.createRoute(article))
                    }
                )
            }

            // --- Details Destination ---
            composable(Screen.Details.route) { backStackEntry ->
                val article = Screen.Details.getArticle(backStackEntry)

                if (article != null) {
                    DetailsRoute(
                        article = article,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }
        }
    }
}