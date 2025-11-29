package com.elgohary.newsapptask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.navigation.NewsNavHost
import com.elgohary.newsapptask.ui.theme.NewsAppTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTaskTheme {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        NewsNavHost(modifier = Modifier.padding(paddingValues))
    }
}
