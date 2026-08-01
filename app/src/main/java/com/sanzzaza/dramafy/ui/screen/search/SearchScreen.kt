@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.sanzzaza.dramafy.ui.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanzzaza.dramafy.ui.component.BookRowCard
import com.sanzzaza.dramafy.ui.component.EmptyState
import com.sanzzaza.dramafy.ui.component.ErrorState
import com.sanzzaza.dramafy.ui.component.LoadingState
import com.sanzzaza.dramafy.ui.component.TagPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", style = MaterialTheme.typography.titleLarge) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("What do you want to watch?") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            Spacer(Modifier.height(12.dp))

            if (suggestions.isNotEmpty() && state.query.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Suggestions",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suggestions.forEach { s ->
                                TagPill(text = s, onClick = { viewModel.useSuggestion(s) })
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && state.results.isEmpty() -> LoadingState()
                    state.error != null && state.results.isEmpty() -> ErrorState(
                        message = state.error ?: "Search failed",
                        onRetry = { viewModel.onQueryChange(state.query) }
                    )
                    state.results.isEmpty() && state.query.isBlank() -> EmptyState(
                        title = "Discover your next favorite",
                        subtitle = "Search by title, tag, or keyword."
                    )
                    state.results.isEmpty() -> EmptyState(
                        title = "No results",
                        subtitle = "Try different keywords."
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.results, key = { it.id }) { drama ->
                            BookRowCard(
                                item = drama,
                                onClick = { onBookClick(drama.id) }
                            )
                        }
                        if (state.hasMore) {
                            item {
                                Surface(
                                    onClick = viewModel::loadMore,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = if (state.isLoading) "Loading…" else "Load more",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
