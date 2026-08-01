package com.sanzzaza.dramafy.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import com.sanzzaza.dramafy.data.model.BookMallResponse
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(
        val data: BookMallResponse,
        val language: String
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DramaRepository,
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var currentLang: String = "en"

    init {
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            preferences.language.collectLatest { lang ->
                if (lang != currentLang) {
                    currentLang = lang
                    load(lang)
                }
            }
        }
    }

    fun refresh() = load(currentLang)

    private fun load(lang: String) {
        _state.value = HomeUiState.Loading
        viewModelScope.launch {
            val result = repository.bookMall(lang)
            _state.value = result.fold(
                onSuccess = { HomeUiState.Success(it, lang) },
                onFailure = { HomeUiState.Error(it.message ?: "Failed to load") }
            )
        }
    }
}
