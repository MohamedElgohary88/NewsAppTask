package com.elgohary.newsapptask.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NewsBottomNavigation(
    items: List<Screen>,
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    NavigationBar {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(screen) },
                icon = {
                    screen.icon?.let {
                        Icon(imageVector = it, contentDescription = screen.label)
                    }
                },
                label = { Text(text = screen.label) }
            )
        }
    }
}