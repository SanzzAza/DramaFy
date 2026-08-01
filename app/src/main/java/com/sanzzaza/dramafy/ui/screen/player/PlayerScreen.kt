@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.sanzzaza.dramafy.ui.screen.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sanzzaza.dramafy.data.model.Episode
import com.sanzzaza.dramafy.ui.component.ErrorState
import com.sanzzaza.dramafy.ui.component.LoadingState
import com.sanzzaza.dramafy.util.Formatters

@OptIn(ExperimentalMaterial3Api::class, UnstableApi::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    val currentEp = state.episodes.getOrNull(state.currentIndex)
    LaunchedEffect(currentEp?.streamUrl) {
        val url = currentEp?.streamUrl
        if (!url.isNullOrBlank()) {
            val item = MediaItem.fromUri(url)
            player.setMediaItem(item)
            player.prepare()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentEp?.let { "Episode ${it.index}" } ?: "Now Playing",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .background(Color.Black)
            ) {
                when {
                    state.isLoading -> LoadingState()
                    state.error != null -> ErrorState(
                        message = state.error ?: "Cannot play",
                        onRetry = viewModel::load
                    )
                    !currentEp?.streamUrl.isNullOrBlank() -> AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                useController = true
                                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                this.player = player
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (state.episodes.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.episodes, key = { it.vid }) { ep ->
                                EpisodeChip(
                                    ep = ep,
                                    selected = state.currentIndex == state.episodes.indexOf(ep),
                                    onClick = { viewModel.selectEpisode(state.episodes.indexOf(ep)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeChip(ep: Episode, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "EP ${ep.index}",
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
