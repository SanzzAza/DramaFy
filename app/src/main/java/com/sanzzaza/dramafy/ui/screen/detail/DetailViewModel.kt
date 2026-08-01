package com.sanzzaza.dramafy.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.BookDetailDto
import com.sanzzaza.dramafy.data.model.SearchItemDto
import com.sanzzaza.dramafy.data.model.SeriesResponse
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val book: BookDetailDto? = null,
    val related: List<SearchItemDto> = emptyList()
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    val isBookmarked: StateFlow<Boolean> = repository.observeIsBookmarked(bookId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var language: String = "en"

    init {
        viewModelScope.launch {
            preferences.language.collectLatest {
                if (it != language) {
                    language = it
                    load()
                }
            }
        }
    }

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.series(bookId, language)
            result.fold(
                onSuccess = { resp: SeriesResponse ->
                    _state.value = DetailUiState(
                        isLoading = false,
                        book = resp.book,
                        related = resp.related
                    )
                },
                onFailure = { t ->
                    // Fallback to /book if /series fails
                    val fallback = repository.book(bookId, language)
                    fallback.fold(
                        onSuccess = { _state.value = DetailUiState(isLoading = false, book = it) },
                        onFailure = { _state.value = DetailUiState(isLoading = false, error = t.message ?: "Failed to load") }
                    )
                }
            )
        }
    }

    fun toggleBookmark() {
        val current = _state.value.book ?: return
        viewModelScope.launch {
            val item = SearchItemDto(
                id = current.id,
                title = current.title,
                cover = current.cover,
                introduction = current.introduction,
                tags = current.tags,
                author = current.author,
                episodeCount = current.episodeCount,
                playCount = current.playCount,
                rating = current.rating
            )
            if (isBookmarked.value) repository.removeBookmark(item.id) else repository.addBookmark(item)
        }
    }
}
