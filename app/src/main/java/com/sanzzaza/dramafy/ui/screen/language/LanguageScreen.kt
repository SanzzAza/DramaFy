package com.sanzzaza.dramafy.ui.screen.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanzzaza.dramafy.data.model.Language
import com.sanzzaza.dramafy.ui.component.ErrorState
import com.sanzzaza.dramafy.ui.component.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language", style = MaterialTheme.typography.titleLarge) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is LanguageUiState.Loading -> LoadingState()
                is LanguageUiState.Error -> ErrorState(s.message, onRetry = viewModel::load)
                is LanguageUiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(s.languages, key = { it.code }) { lang ->
                        LanguageRow(
                            language = lang,
                            selected = lang.code == s.selected,
                            onClick = { viewModel.select(lang.code) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(
    language: Language,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        color = bg,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ) {
                Text(
                    text = language.code.uppercase().take(2),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.name.ifBlank { language.code },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = language.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
