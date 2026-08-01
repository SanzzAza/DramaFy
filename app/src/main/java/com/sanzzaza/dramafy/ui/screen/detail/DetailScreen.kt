@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.sanzzaza.dramafy.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sanzzaza.dramafy.data.model.Drama
import com.sanzzaza.dramafy.data.model.Episode
import com.sanzzaza.dramafy.ui.component.ErrorState
import com.sanzzaza.dramafy.ui.component.LoadingState
import com.sanzzaza.dramafy.ui.component.TagPill
import com.sanzzaza.dramafy.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onPlay: (String, Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val drama = state.drama
            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(
                    message = state.error ?: "Failed to load",
                    onRetry = viewModel::load
                )
                drama != null -> DetailContent(
                    drama = drama,
                    episodes = state.episodes,
                    onPlay = onPlay
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    drama: Drama,
    episodes: List<Episode>,
    onPlay: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { HeroCover(drama) }
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = drama.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (drama.author.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "by ${drama.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (drama.playCount > 0) {
                        StatBadge("${Formatters.compactNumber(drama.playCount)} plays")
                        Spacer(Modifier.width(8.dp))
                    }
                    if (drama.episodeCount > 0) {
                        StatBadge("${drama.episodeCount} eps")
                    }
                }
                if (drama.tags.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        drama.tags.take(6).forEach { TagPill(it) }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onPlay(drama.id, 1) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play Episode 1", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (drama.introduction.isNotBlank()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = drama.introduction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (episodes.isNotEmpty()) {
            item {
                Text(
                    text = "Episodes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(episodes, key = { it.vid }) { ep ->
                        EpisodeChip(ep, onClick = { onPlay(drama.id, ep.index) })
                    }
                }
            }
        }
    }
}

private val DetailUiState.d: Drama?
    get() = drama

@Composable
private fun HeroCover(drama: Drama) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 11f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (drama.cover.isNotBlank()) {
            AsyncImage(
                model = com.sanzzaza.dramafy.util.ImageUrl.cover(drama.cover),
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
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 200f
                    )
                )
        )
    }
}

@Composable
private fun StatBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EpisodeChip(ep: Episode, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(130.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${ep.index}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "EP ${ep.index}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (ep.duration > 0) {
                    Text(
                        text = Formatters.duration(ep.duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
