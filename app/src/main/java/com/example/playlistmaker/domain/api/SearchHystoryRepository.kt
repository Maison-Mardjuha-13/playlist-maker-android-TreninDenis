package com.example.playlistmaker.domain.api

interface SearchHistoryRepository {
    suspend fun addEntry(word: String)
    suspend fun getHistory(): List<String>
    suspend fun clearHistory()
    fun getHistoryFlow(): kotlinx.coroutines.flow.Flow<List<String>>
}