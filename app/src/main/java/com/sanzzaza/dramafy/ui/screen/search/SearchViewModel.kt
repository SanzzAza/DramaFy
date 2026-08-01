package com.sanzzaza.dramafy.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.SearchItemDto
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchItemDto> = emptyList(),
    val hasMore: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var language: String = "en"
    private var debounceJob: Job? = null
    private var suggestJob: Job? = null

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.language.collectLatest { language = it }
        }
    }

    fun onQueryChange(q: String) {
        _state.value = _state.value.copy(query = q, error = null)
        debounceJob?.cancel()
        suggestJob?.cancel()
        if (q.isBlank()) {
            _suggestions.value = emptyList()
            _state.value = _state.value.copy(results = emptyList(), hasMore = false, isLoading = false)
            return
        }
        debounceJob = viewModelScope.launch {
            delay(350)
            performSearch(q, reset = true)
        }
        suggestJob = viewModelScope.launch {
            delay(250)
            val res = repository.suggest(q, language)
            res.onSuccess { _suggestions.value = it.suggestions.filter { s -> s.contains(q, ignoreCase = true) }.take(8) }
        }
    }

    fun loadMore() {
        if (_state.value.isLoading || !_state.value.hasMore) return
        performSearch(_state.value.query, reset = false)
    }

    fun useSuggestion(text: String) {
        onQueryChange(text)
    }

    private fun performSearch(q: String, reset: Boolean) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val offset = if (reset) 0 else _state.value.results.size
            val res = repository.search(q, language, offset = offset, limit = 30)
            res.fold(
                onSuccess = { resp ->
                    val merged = if (reset) resp.items else _state.value.results + resp.items
                    _state.value = _state.value.copy(
                        isLoading = false,
                        results = merged,
                        hasMore = resp.hasMore
                    )
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Search failed")
                }
            )
        }
    }
}
