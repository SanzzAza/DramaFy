package com.sanzzaza.dramafy.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dramafy_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_LANGUAGE = stringPreferencesKey("selected_language")
    private val KEY_DARK_MODE = stringPreferencesKey("dark_mode") // "system" | "light" | "dark"

    val language: Flow<String> = context.dataStore.data.map { prefs: Preferences ->
        prefs[KEY_LANGUAGE] ?: "en"
    }

    val darkMode: Flow<String> = context.dataStore.data.map { prefs: Preferences ->
        prefs[KEY_DARK_MODE] ?: "system"
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = code }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { it[KEY_DARK_MODE] = mode }
    }
}
