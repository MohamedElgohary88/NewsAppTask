package com.elgohary.newsapptask.presentation.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.elgohary.newsapptask.R
import com.elgohary.newsapptask.presentation.designsystem.common.ShimmerEffect
import com.elgohary.newsapptask.presentation.designsystem.Dimens

@Composable
fun ArticleHeaderImage(imageUrl: String?, title: String?) {
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val painterState = painter.state
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = Dimens.CornerLG, bottomEnd = Dimens.CornerLG))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when (painterState.collectAsState().value) {
            is AsyncImagePainter.State.Loading -> ShimmerEffect(modifier = Modifier.fillMaxSize())
            is AsyncImagePainter.State.Error -> Text(
                text = stringResource(R.string.no_image),
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