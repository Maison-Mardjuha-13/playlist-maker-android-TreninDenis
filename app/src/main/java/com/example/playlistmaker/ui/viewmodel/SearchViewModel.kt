import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.ui.viewmodel.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private var searchJob: Job? = null
    private var lastSearchQuery: String = ""
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    private var currentSearchAddedToHistory = false

    init {
        loadSearchHistory()
    }

    fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        lastSearchQuery = query
        searchJob?.cancel()

        _searchScreenState.value = SearchState.Searching

        searchJob = viewModelScope.launch {
            delay(500)

            try {
                val tracks = searchRepository.searchTracks(query)
                _searchScreenState.value = SearchState.Success(tracks)


                addToHistory(query)
            } catch (e: Exception) {
                _searchScreenState.value = SearchState.Fail(e.message ?: "Unknown error")
            }
        }
    }

    fun performSearchFromHistory(query: String) {
        if (query.isBlank()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        lastSearchQuery = query
        searchJob?.cancel()

        _searchScreenState.value = SearchState.Searching

        searchJob = viewModelScope.launch {
            try {
                val tracks = searchRepository.searchTracks(query)
                _searchScreenState.value = SearchState.Success(tracks)
            } catch (e: Exception) {
                _searchScreenState.value = SearchState.Fail(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.getHistoryFlow().collect { history ->
                _searchHistory.value = history
            }
        }
    }

    private suspend fun addToHistory(query: String) {
        if (query.isNotBlank()) {
            searchHistoryRepository.addEntry(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearHistory()
        }
    }

    fun retryLastSearch() {
        if (lastSearchQuery.isNotEmpty()) {
            performSearchFromHistory(lastSearchQuery)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        lastSearchQuery = ""
        _searchScreenState.value = SearchState.Initial
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val tracksRepository = Creator.getTracksRepository()
                    val searchHistoryRepository = Creator.getSearchHistoryRepository(context)
                    return SearchViewModel(tracksRepository, searchHistoryRepository) as T
                }
            }
        }
    }

}