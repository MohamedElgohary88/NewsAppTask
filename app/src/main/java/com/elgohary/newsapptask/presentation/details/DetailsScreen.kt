package com.elgohary.newsapptask.presentation.details

import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.common.ShimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    article: Article,
    navController: NavHostController? = null,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val savedUrls by viewModel.savedUrls.collectAsState()
    val alreadySaved = article.url?.let { savedUrls.contains(it) } == true
    val expandedState = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DetailsTopBar(onBack = { navController?.popBackStack() })
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !alreadySaved,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SaveFab(onClick = {
                    viewModel.saveArticle(article)
                    Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()
                })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ArticleHeaderImage(imageUrl = article.urlToImage, title = article.title)

            Column(modifier = Modifier.padding(16.dp)) {
                ArticleTitleAndMeta(
                    title = article.title.orEmpty(),
                    publishedAt = article.publishedAt.orEmpty(),
                    sourceName = article.source?.name
                )

                Spacer(modifier = Modifier.height(16.dp))

                ArticleDescription(
                    description = article.description,
                    content = article.content,
                    isExpanded = expandedState.value,
                    onToggleExpand = { expandedState.value = !expandedState.value },
                    articleUrl = article.url
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Full Article") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun SaveFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Bookmark, contentDescription = "Save Article")
    }
}

@Composable
private fun ArticleHeaderImage(imageUrl: String?, title: String?) {
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val painterState = painter.state
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when (painterState.collectAsState().value) {
            is AsyncImagePainter.State.Loading -> ShimmerEffect(modifier = Modifier.fillMaxSize())
            is AsyncImagePainter.State.Error -> Text(
                text = "No Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ArticleTitleAndMeta(title: String, publishedAt: String, sourceName: String?) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!sourceName.isNullOrBlank()) {
            Text(
                text = sourceName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = publishedAt,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ArticleDescription(
    description: String?,
    content: String?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    articleUrl: String?
) {
    val context = LocalContext.current
    val descSafe = description.orEmpty()
    val contentSafe = content.orEmpty()

    val collapsedCharLimit = 220
    val needsCollapse = descSafe.length > collapsedCharLimit && !isExpanded
    val visibleText = if (needsCollapse) descSafe.take(collapsedCharLimit) + "…" else descSafe

    Text(
        text = visibleText,
        style = MaterialTheme.typography.bodyMedium
    )

    if (needsCollapse || (!isExpanded && contentSafe.isNotBlank() && descSafe.isNotBlank())) {
        ReadMoreLink(onClick = onToggleExpand)
    }

    AnimatedVisibility(visible = isExpanded) {
        Column {
            if (contentSafe.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = contentSafe,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!articleUrl.isNullOrBlank()) {
                OpenInBrowserLink(onOpen = {
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(context, Uri.parse(articleUrl))
                })
            }
        }
    }
}

@Composable
private fun ReadMoreLink(onClick: () -> Unit) {
    Text(
        text = "Read more",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Read more" }
    )
}

@Composable
private fun OpenInBrowserLink(onOpen: () -> Unit) {
    Text(
        text = "Open in browser",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = onOpen)
            .semantics { contentDescription = "Open article in browser" }
    )
}