package com.elgohary.newsapptask.presentation.designsystem.common

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleCard(
    article: Article,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingMD, vertical = Dimens.SpacingSM)
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(Dimens.CornerLG),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.SpacingSM)
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingMD)) {
            val painter = rememberAsyncImagePainter(model = article.urlToImage)
            val painterState = painter.state

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight)
                    .clip(RoundedCornerShape(Dimens.CornerMD))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                when (painterState.collectAsState().value) {
                    is AsyncImagePainter.State.Loading -> {
                        ShimmerEffect(modifier = Modifier.fillMaxSize())
                    }

                    is AsyncImagePainter.State.Error -> {
                        ImagePlaceholder()
                    }

                    else -> {
                        AsyncImage(
                            model = article.urlToImage,
                            contentDescription = article.title ?: stringResource(R.string.no_image),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingSM))

            Text(
                text = article.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXS))

            Text(
                text = article.description.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_image),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
