package com.campus.lostfound.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val dataStore = context.dataStore
    
    private object PreferenceKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode") // "system", "light", "dark"
        val THEME_COLOR = stringPreferencesKey("theme_color") // ThemeColor enum name
        val IS_GUEST_MODE = booleanPreferencesKey("is_guest_mode") // Guest mode flag
        
        // Profile cache (persistent)
        val CACHED_USER_ID = stringPreferencesKey("cached_user_id")
        val CACHED_USER_NAME = stringPreferencesKey("cached_user_name")
        val CACHED_USER_EMAIL = stringPreferencesKey("cached_user_email")
        val CACHED_USER_PHOTO = stringPreferencesKey("cached_user_photo")
        val CACHED_USER_TIME = stringPreferencesKey("cached_user_time")
    }
    
    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: true
        }
    
    val soundEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.SOUND_ENABLED] ?: true
        }
    
    val themeModeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.THEME_MODE] ?: "light"
        }
    
    val themeColorFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] ?: "TEAL"
        }
    
    val isGuestModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.IS_GUEST_MODE] ?: false
        }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SOUND_ENABLED] = enabled
        }
    }
    
    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode
        }
    }
    
    suspend fun setThemeColor(colorName: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] = colorName
        }
    }
    
    suspend fun setGuestMode(isGuest: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_GUEST_MODE] = isGuest
        }
    }
    
    // Profile cache methods
    suspend fun cacheUserProfile(userId: String, name: String, email: String, photoUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.CACHED_USER_ID] = userId
            preferences[PreferenceKeys.CACHED_USER_NAME] = name
            preferences[PreferenceKeys.CACHED_USER_EMAIL] = email
            preferences[PreferenceKeys.CACHED_USER_PHOTO] = photoUrl
            preferences[PreferenceKeys.CACHED_USER_TIME] = System.currentTimeMillis().toString()
        }
    }
    
    suspend fun getCachedUserProfile(): Triple<String, String, String>? {
        var result: Triple<String, String, String>? = null
        dataStore.data.collect { preferences ->
            val userId = preferences[PreferenceKeys.CACHED_USER_ID] ?: return@collect
            val name = preferences[PreferenceKeys.CACHED_USER_NAME] ?: ""
            val email = preferences[PreferenceKeys.CACHED_USER_EMAIL] ?: ""
            result = Triple(userId, name, email)
        }
        return result
    }
    
    val cachedUserNameFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.CACHED_USER_NAME] ?: ""
        }
    
    val cachedUserEmailFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.CACHED_USER_EMAIL] ?: ""
        }
    
    val cachedUserPhotoFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.CACHED_USER_PHOTO] ?: ""
        }
}