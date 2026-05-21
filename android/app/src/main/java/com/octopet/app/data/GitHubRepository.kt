package com.octopet.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("octopet_prefs")

private val KEY_USERNAME     = stringPreferencesKey("github_username")
private val KEY_TOKEN        = stringPreferencesKey("github_token")
private val KEY_SIGNUP_DATE  = stringPreferencesKey("pet_signup_date")
private val KEY_VARIANT_SEED = longPreferencesKey("pet_variant_seed")

class GitHubRepository(private val context: Context) {

    val credentials: Flow<Pair<String, String>> = context.dataStore.data.map { prefs ->
        (prefs[KEY_USERNAME] ?: "") to (prefs[KEY_TOKEN] ?: "")
    }

    val petProfile: Flow<PetProfile?> = context.dataStore.data.map { prefs ->
        val date = prefs[KEY_SIGNUP_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val seed = prefs[KEY_VARIANT_SEED]
        if (date != null && seed != null) PetProfile(date, seed) else null
    }

    suspend fun saveCredentials(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = username
            prefs[KEY_TOKEN]    = token
        }
    }

    suspend fun savePetProfile(profile: PetProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SIGNUP_DATE]  = profile.signupDate.toString()
            prefs[KEY_VARIANT_SEED] = profile.variantSeed
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { it.clear() }
    }
}
