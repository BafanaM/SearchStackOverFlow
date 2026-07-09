package com.example.stackoverflow.core.network.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.recentSearchesDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_searches")

private val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
private const val MAX_RECENT_SEARCHES = 10

@Singleton
class DataStoreRecentSearchesRepository @Inject constructor(
    @ApplicationContext context: Context,
) : RecentSearchesRepository {

    private val dataStore = context.recentSearchesDataStore

    override val recentSearches: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[RECENT_SEARCHES_KEY]?.let { decode(it) } ?: emptyList()
    }

    override suspend fun addSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        dataStore.edit { preferences ->
            val current = preferences[RECENT_SEARCHES_KEY]?.let { decode(it) } ?: emptyList()
            val updated = listOf(trimmed) + current.filterNot { it.equals(trimmed, ignoreCase = true) }
            preferences[RECENT_SEARCHES_KEY] = Json.encodeToString(updated.take(MAX_RECENT_SEARCHES))
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences -> preferences.remove(RECENT_SEARCHES_KEY) }
    }

    private fun decode(raw: String): List<String> =
        runCatching { Json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
}
