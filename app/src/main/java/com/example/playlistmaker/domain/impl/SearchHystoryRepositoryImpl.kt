package com.example.playlistmaker.domain.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SearchHistoryRepository {


    private val preferencesKey = stringPreferencesKey("search_history")

    override suspend fun addEntry(word: String) {
        if (word.trim().isEmpty()) {
            return
        }

        dataStore.edit { mutablePreferences: MutablePreferences ->
            val historyString = mutablePreferences[preferencesKey] ?: ""
            val history = if (historyString.isNotEmpty()) {
                historyString.split(SEPARATOR).toMutableList()
            } else {
                mutableListOf()
            }

            history.remove(word)
            history.add(0, word)

            val subList = if (history.size > MAX_ENTRIES) {
                history.subList(0, MAX_ENTRIES)
            } else {
                history
            }

            val updatedString = subList.joinToString(separator = SEPARATOR)
            mutablePreferences[preferencesKey] = updatedString
        }
    }

    override suspend fun getHistory(): List<String> {
        return dataStore.data.map { preferences ->
            (preferences[preferencesKey] ?: "")
                .split(SEPARATOR)
                .filter { it.isNotBlank() }
        }.first()
    }

    override fun getHistoryFlow(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            (preferences[preferencesKey] ?: "")
                .split(SEPARATOR)
                .filter { it.isNotBlank() }
        }
    }

    override suspend fun clearHistory() {
        dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences.remove(preferencesKey)
        }
    }

    companion object {
        private const val MAX_ENTRIES = 10
        private const val SEPARATOR = ","
    }
}

