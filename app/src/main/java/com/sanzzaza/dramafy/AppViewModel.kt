package com.sanzzaza.dramafy

import androidx.lifecycle.ViewModel
import com.sanzzaza.dramafy.data.local.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    preferences: PreferencesRepository
) : ViewModel() {
    val darkMode: StateFlow<String> = preferences.darkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, "system")
}
