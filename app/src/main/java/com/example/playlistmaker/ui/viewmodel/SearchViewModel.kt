package com.example.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState

    private var searchJob: Job? = null

    fun searchTrack(expression: String) {
        println("SearchViewModel: Starting search for '$expression'")
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchScreenState.value = SearchState.Searching
            delay(500)

            try {
                val foundList = tracksRepository.searchTracks(expression)
                println("SearchViewModel: Search completed, found ${foundList.size} tracks")

                if (foundList.isEmpty()) {
                    _searchScreenState.value = SearchState.Fail("Ничего не найдено")
                    println("SearchViewModel: No tracks found")
                } else {
                    _searchScreenState.value = SearchState.Success(foundList)
                    println("SearchViewModel: Search successful, showing ${foundList.size} tracks")
                }
            } catch (e: Exception) {
                println("SearchViewModel: Search error - ${e.message}")
                _searchScreenState.value = SearchState.Fail(e.message ?: "Ошибка поиска")
            }
        }
    }

    fun reset() {
        _searchScreenState.update { SearchState.Initial }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        tracksRepository = Creator.getTracksRepository()
                    ) as T
                }
            }
        }
    }
}