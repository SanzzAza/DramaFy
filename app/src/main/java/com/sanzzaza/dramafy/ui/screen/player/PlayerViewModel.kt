package com.sanzzaza.dramafy.ui.screen.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.Episode
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val episodes: List<Episode> = emptyList(),
    val currentIndex: Int = 0,
    val bookId: String = ""
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val bookId: String = checkNotNull(savedStateHandle["bookId"])
    val startEpisodeIndex: Int = (savedStateHandle["episodeIndex"] as? Int) ?: 0

    private val _state = MutableStateFlow(PlayerUiState(bookId = bookId))
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

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
            val res = repository.multiVideo(bookId, language)
            res.fold(
                onSuccess = { eps ->
                    val target = (startEpisodeIndex - 1).coerceIn(0, (eps.size - 1).coerceAtLeast(0))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        episodes = eps,
                        currentIndex = target
                    )
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Unable to load video")
                }
            )
        }
    }

    fun selectEpisode(index: Int) {
        if (index in _state.value.episodes.indices) {
            _state.value = _state.value.copy(currentIndex = index)
        }
    }
}
