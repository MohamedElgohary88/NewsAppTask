package com.elgohary.newsapptask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.elgohary.newsapptask.presentation.designsystem.NewsAppTaskTheme
import com.elgohary.newsapptask.presentation.navigation.NewsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTaskTheme {
                NewsNavHost(modifier = Modifier)
            }
        }
    }
}