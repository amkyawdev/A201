package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "studio_settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val GEMINI_KEY = stringPreferencesKey("gemini_api_key")
        val ELEVENLABS_KEY = stringPreferencesKey("elevenlabs_api_key")
        val ASSEMBLYAI_KEY = stringPreferencesKey("assemblyai_api_key")
        val DEFAULT_LANGUAGE = stringPreferencesKey("default_language")
        val DEFAULT_ASPECT_RATIO = stringPreferencesKey("default_aspect_ratio")
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[GEMINI_KEY] ?: ""
    }

    val elevenLabsApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ELEVENLABS_KEY] ?: ""
    }

    val assemblyAiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ASSEMBLYAI_KEY] ?: ""
    }

    val defaultLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_LANGUAGE] ?: "dual"
    }

    val defaultAspectRatio: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_ASPECT_RATIO] ?: "9:16"
    }

    suspend fun saveApiKeys(gemini: String, elevenLabs: String, assemblyAi: String) {
        context.dataStore.edit { prefs ->
            prefs[GEMINI_KEY] = gemini
            prefs[ELEVENLABS_KEY] = elevenLabs
            prefs[ASSEMBLYAI_KEY] = assemblyAi
        }
    }

    suspend fun saveDefaults(language: String, aspectRatio: String) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_LANGUAGE] = language
            prefs[DEFAULT_ASPECT_RATIO] = aspectRatio
        }
    }
}
