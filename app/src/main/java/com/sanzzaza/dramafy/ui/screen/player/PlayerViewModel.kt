package com.sanzzaza.dramafy.ui.screen.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.VideoSourceDto
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
    val sources: List<VideoSourceDto> = emptyList(),
    val currentIndex: Int = 0,
    val title: String = "",
    val episodeIndex: Int = 0
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val bookId: String = checkNotNull(savedStateHandle["bookId"])
    val episodeIndex: Int = checkNotNull(savedStateHandle["episodeIndex"])

    private val _state = MutableStateFlow(PlayerUiState(episodeIndex = episodeIndex))
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
                onSuccess = { mv ->
                    val sorted = mv.videoList.sortedByDescending { parseQualityRank(it.quality) }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        sources = sorted,
                        currentIndex = 0
                    )
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Unable to load video")
                }
            )
        }
    }

    fun selectSource(index: Int) {
        if (index in _state.value.sources.indices) {
            _state.value = _state.value.copy(currentIndex = index)
        }
    }

    private fun parseQualityRank(quality: String): Int {
        return when {
            quality.contains("1080", true) -> 1080
            quality.contains("720", true) -> 720
            quality.contains("480", true) -> 480
            quality.contains("360", true) -> 360
            quality.contains("240", true) -> 240
            else -> 0
        }
    }
}
