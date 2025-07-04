package com.uniguard.trackable.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class PreferenceManager(
    private val context: Context
) {
    companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    suspend fun saveUserToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_TOKEN] = token
        }
    }

    fun getUserToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USER_TOKEN]
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}