package com.seva.scripture.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.seva.scripture.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "scripture_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val language = stringPreferencesKey("language")
        val fontScale = floatPreferencesKey("font_scale")
        val darkMode = booleanPreferencesKey("dark_mode")
        val showTransliteration = booleanPreferencesKey("show_transliteration")
        val showPhilosophical = booleanPreferencesKey("show_philosophical")
        val dailyNotification = booleanPreferencesKey("daily_notification")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { pref ->
        AppSettings(
            languageCode = pref[Keys.language] ?: "en",
            fontScale = pref[Keys.fontScale] ?: 1f,
            darkMode = pref[Keys.darkMode] ?: false,
            transliterationVisible = pref[Keys.showTransliteration] ?: true,
            philosophicalVisible = pref[Keys.showPhilosophical] ?: true,
            dailyNotificationEnabled = pref[Keys.dailyNotification] ?: true
        )
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { it[Keys.language] = languageCode }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[Keys.fontScale] = scale }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.darkMode] = enabled }
    }

    suspend fun setTransliteration(enabled: Boolean) {
        context.dataStore.edit { it[Keys.showTransliteration] = enabled }
    }

    suspend fun setPhilosophical(enabled: Boolean) {
        context.dataStore.edit { it[Keys.showPhilosophical] = enabled }
    }

    suspend fun setDailyNotification(enabled: Boolean) {
        context.dataStore.edit { it[Keys.dailyNotification] = enabled }
    }
}
