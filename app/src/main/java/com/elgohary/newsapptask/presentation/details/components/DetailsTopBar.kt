package com.elgohary.newsapptask.presentation.details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.res.stringResource
import com.elgohary.newsapptask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = { Text(text = stringResource(R.string.full_article)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
    )
}