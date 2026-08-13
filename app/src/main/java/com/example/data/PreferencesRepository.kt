package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "telejob_settings")

class PreferencesRepository(private val context: Context) {

    companion object {
        val KEY_TELEGRAM_API_ID = stringPreferencesKey("telegram_api_id")
        val KEY_TELEGRAM_API_HASH = stringPreferencesKey("telegram_api_hash")
        val KEY_TELEGRAM_PHONE = stringPreferencesKey("telegram_phone")
        val KEY_TELEGRAM_LOGGED_IN = booleanPreferencesKey("telegram_logged_in")
        val KEY_GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val KEY_SERVICE_ACTIVE = booleanPreferencesKey("service_active")
        val KEY_AUTO_TRANSLATE = booleanPreferencesKey("auto_translate")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    private fun getBuildConfigString(value: String?): String {
        return if (value.isNullOrBlank() || value.startsWith("MY_") || value == "null") "" else value
    }

    val telegramApiId: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_TELEGRAM_API_ID]
        if (!saved.isNullOrBlank()) saved else getBuildConfigString(runCatching { BuildConfig.API_ID }.getOrNull())
    }

    val telegramApiHash: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_TELEGRAM_API_HASH]
        if (!saved.isNullOrBlank()) saved else getBuildConfigString(runCatching { BuildConfig.API_HASH }.getOrNull())
    }

    val telegramPhone: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_TELEGRAM_PHONE]
        if (!saved.isNullOrBlank()) saved else getBuildConfigString(runCatching { BuildConfig.PHONE_NUMBER }.getOrNull())
    }

    val isTelegramLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_TELEGRAM_LOGGED_IN] ?: false
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_GEMINI_API_KEY]
        if (!saved.isNullOrBlank()) saved else getBuildConfigString(runCatching { BuildConfig.GEMINI_API_KEY }.getOrNull())
    }

    val isServiceActive: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SERVICE_ACTIVE] ?: true
    }

    val isAutoTranslateEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_TRANSLATE] ?: true
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun saveTelegramCredentials(apiId: String, apiHash: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TELEGRAM_API_ID] = apiId
            prefs[KEY_TELEGRAM_API_HASH] = apiHash
            prefs[KEY_TELEGRAM_PHONE] = phone
        }
    }

    suspend fun setTelegramLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TELEGRAM_LOGGED_IN] = loggedIn
        }
    }

    suspend fun saveGeminiApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GEMINI_API_KEY] = key
        }
    }

    suspend fun setServiceActive(active: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SERVICE_ACTIVE] = active
        }
    }

    suspend fun setAutoTranslate(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_TRANSLATE] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
