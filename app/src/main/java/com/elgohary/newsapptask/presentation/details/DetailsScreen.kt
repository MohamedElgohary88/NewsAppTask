package com.elgohary.newsapptask.presentation.details

import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.elgohary.newsapptask.domain.model.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    article: Article,
    navController: NavHostController? = null,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val savedUrls by viewModel.savedUrls.collectAsState()
    val isSaved = article.url != null && savedUrls.contains(article.url)
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Full article") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSaved) {
                FloatingActionButton(onClick = {
                    viewModel.saveArticle(article)
                    Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Filled.Bookmark, contentDescription = "Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val painter = rememberAsyncImagePainter(model = article.urlToImage)
            val painterState = painter.state

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (painterState) {
                        is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                        is AsyncImagePainter.State.Error -> Text(text = "No Image")
                        else -> AsyncImage(
                            model = article.urlToImage,
                            contentDescription = article.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = article.title ?: "",
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.publishedAt ?: "",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            val description = article.description.orEmpty()
            val contentFull = article.content.orEmpty()
            val displayedText = if (expanded || description.length <= 200) description else description.take(200) + "..."

            Text(
                text = if (description.isNotBlank()) "$displayedText  Read more" else "Read more",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    if (!expanded) {
                        setExpanded(true)
                    } else {
                        // When fully expanded, show more details inline (content)
                        // And still allow opening Custom Tab via second section below
                    }
                }
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = contentFull,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
                val url = article.url
                if (!url.isNullOrEmpty()) {
                    Text(
                        text = "Open in browser",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.launchUrl(context, Uri.parse(url))
                        }
                    )
                }
            }
        }
    }
}
