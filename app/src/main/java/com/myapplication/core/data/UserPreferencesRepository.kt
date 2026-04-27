package com.myapplication.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    val userToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }

    val fcmToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[FCM_TOKEN]
    }

    val isBiometricEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED] ?: false
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun saveFcmToken(token: String) {
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN)
            // No borramos BIOMETRIC_ENABLED aquí para que el usuario no tenga que activarlo siempre
        }
    }
    
    suspend fun fullClear() {
        dataStore.edit { it.clear() }
    }
}
