package com.sanzzaza.dramafy.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanzzaza.dramafy.data.model.BookGroupDto
import com.sanzzaza.dramafy.ui.component.BannerCarousel
import com.sanzzaza.dramafy.ui.component.BookPosterCard
import com.sanzzaza.dramafy.ui.component.ErrorState
import com.sanzzaza.dramafy.ui.component.LoadingState
import com.sanzzaza.dramafy.ui.component.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onLanguageClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DramaFy",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    Surface(
                        onClick = onLanguageClick,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = state.languageOrDefault(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.padding(horizontal = 4.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = state) {
                is HomeUiState.Loading -> LoadingState()
                is HomeUiState.Error -> ErrorState(s.message, onRetry = viewModel::refresh)
                is HomeUiState.Success -> HomeContent(
                    state = s,
                    onBookClick = onBookClick,
                    onSearchClick = onSearchClick
                )
            }
        }
    }
}

private fun HomeUiState.languageOrDefault(): String = when (this) {
    is HomeUiState.Success -> language.uppercase()
    else -> "EN"
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onBookClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val data = state.data
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
    ) {
        // Decorative gradient strip
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        // Search bar
        item {
            Surface(
                onClick = onSearchClick,
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Search dramas, series, tags…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        }

        if (data.banners.isNotEmpty()) {
            item {
                BannerCarousel(
                    banners = data.banners,
                    onBannerClick = { onBookClick(it.bookId) }
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }

        // Trending horizontal list (first group)
        val firstGroup = data.groups.firstOrNull { g -> g.books.isNotEmpty() }
        if (firstGroup != null) {
            item {
                SectionHeader(
                    title = "Trending Now",
                    subtitle = "Top picks for you"
                )
            }
            item {
                BookRow(
                    group = firstGroup,
                    onBookClick = onBookClick
                )
            }
        }

        // Remaining groups (skip empty) — render each group as a single item with Column inside
        val remainingGroups = data.groups.drop(
            if (data.groups.firstOrNull() == firstGroup && firstGroup != null) 1 else 0
        ).filter { it.books.isNotEmpty() }
        items(remainingGroups, key = { it.id.ifBlank { it.name } }) { group ->
            Column {
                SectionHeader(title = group.name.ifBlank { "Recommended" })
                BookRow(
                    group = group,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Composable
private fun BookRow(
    group: BookGroupDto,
    onBookClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(group.books, key = { it.id }) { book ->
            BookPosterCard(
                item = book,
                onClick = { onBookClick(book.id) }
            )
        }
    }
}
