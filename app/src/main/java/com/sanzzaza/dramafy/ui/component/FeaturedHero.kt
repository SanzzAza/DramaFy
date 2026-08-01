package com.sanzzaza.dramafy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sanzzaza.dramafy.data.model.Drama
import com.sanzzaza.dramafy.util.Formatters
import kotlinx.coroutines.delay

/**
 * Featured hero — auto-scrolling carousel of top picks. Used in place of a
 * banner section because the upstream API does not return a separate banner
 * feed in the public endpoints.
 */
@Composable
fun FeaturedHero(
    items: List<Drama>,
    onItemClick: (Drama) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(items) {
        if (items.size > 1) {
            while (true) {
                delay(4500)
                val next = (pagerState.currentPage + 1) % items.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column(modifier = modifier.padding(contentPadding)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 11f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            HorizontalPager(state = pagerState, pageSpacing = 12.dp, modifier = Modifier.fillMaxSize()) { page ->
                val drama = items[page]
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent,
                    onClick = { onItemClick(drama) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (drama.cover.isNotBlank()) {
                            AsyncImage(
                                model = drama.cover,
                                contentDescription = drama.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.75f)
                                        ),
                                        startY = 100f
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = drama.title,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (drama.introduction.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = drama.introduction,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (drama.playCount > 0 || drama.episodeCount > 0) {
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (drama.playCount > 0) {
                                        Text(
                                            text = "${Formatters.compactNumber(drama.playCount)} plays",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                    }
                                    if (drama.episodeCount > 0) {
                                        Spacer(Modifier.size(width = 12.dp, height = 0.dp))
                                        Text(
                                            text = "${drama.episodeCount} episodes",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // Pager indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(items.size) { i ->
                val active = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = if (active) 18.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}
