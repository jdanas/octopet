package com.octopet.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("octopet_prefs")

private val KEY_USERNAME = stringPreferencesKey("github_username")
private val KEY_TOKEN    = stringPreferencesKey("github_token")

class GitHubRepository(private val context: Context) {

    val credentials: Flow<Pair<String, String>> = context.dataStore.data.map { prefs ->
        (prefs[KEY_USERNAME] ?: "") to (prefs[KEY_TOKEN] ?: "")
    }

    suspend fun saveCredentials(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = username
            prefs[KEY_TOKEN]    = token
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { it.clear() }
    }
}
