package com.sanzzaza.dramafy.ui.screen.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.LanguageDto
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LanguageUiState {
    data object Loading : LanguageUiState
    data class Error(val message: String) : LanguageUiState
    data class Success(
        val languages: List<LanguageDto>,
        val selected: String
    ) : LanguageUiState
}

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LanguageUiState>(LanguageUiState.Loading)
    val state: StateFlow<LanguageUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = LanguageUiState.Loading
        viewModelScope.launch {
            val current = preferences.language.first()
            val res = repository.languages()
            res.fold(
                onSuccess = { list ->
                    val merged = if (list.any { it.code == current }) list
                    else listOf(LanguageDto(code = current, name = current)) + list
                    _state.value = LanguageUiState.Success(merged, current)
                },
                onFailure = { _state.value = LanguageUiState.Error(it.message ?: "Failed") }
            )
        }
    }

    fun select(code: String) {
        viewModelScope.launch { preferences.setLanguage(code) }
        val current = _state.value
        if (current is LanguageUiState.Success) {
            _state.value = current.copy(selected = code)
        }
    }
}
