package com.sanzzaza.dramafy.ui.screen.bookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanzzaza.dramafy.ui.component.BookRowCard
import com.sanzzaza.dramafy.ui.component.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val items by viewModel.bookmarks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (items.isEmpty()) {
                EmptyState(
                    title = "No bookmarks yet",
                    subtitle = "Save your favorite dramas for later."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                BookRowCard(
                                    item = com.sanzzaza.dramafy.data.model.SearchItemDto(
                                        id = item.id,
                                        title = item.title,
                                        cover = item.cover,
                                        introduction = item.introduction,
                                        tags = item.tags,
                                        author = item.author,
                                        episodeCount = item.episodeCount
                                    ),
                                    onClick = { onBookClick(item.id) }
                                )
                            }
                            IconButton(onClick = { viewModel.remove(item.id) }) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
