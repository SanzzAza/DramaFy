package com.sanzzaza.dramafy.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.Drama
import com.sanzzaza.dramafy.data.model.Episode
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
    val drama: Drama? = null,
    val episodes: List<Episode> = emptyList()
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
            // Try /series first (gives chapters + richer metadata)
            val seriesResult = repository.series(bookId, language)
            seriesResult.fold(
                onSuccess = { (drama, eps) ->
                    _state.value = DetailUiState(
                        isLoading = false,
                        drama = drama,
                        episodes = eps
                    )
                },
                onFailure = { seriesErr ->
                    // Fallback to /book
                    val bookResult = repository.bookDetail(bookId, language)
                    bookResult.fold(
                        onSuccess = { drama ->
                            _state.value = DetailUiState(isLoading = false, drama = drama)
                        },
                        onFailure = { bookErr ->
                            _state.value = DetailUiState(
                                isLoading = false,
                                error = bookErr.message ?: seriesErr.message ?: "Failed to load drama"
                            )
                        }
                    )
                }
            )
        }
    }

    fun toggleBookmark() {
        val current = _state.value.drama ?: return
        viewModelScope.launch {
            if (isBookmarked.value) repository.removeBookmark(current.id)
            else repository.addBookmark(current)
        }
    }
}
