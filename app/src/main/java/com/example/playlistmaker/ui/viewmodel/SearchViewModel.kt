import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.ui.viewmodel.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: TracksRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private var searchJob: Job? = null
    private var lastSearchQuery: String = ""

    fun searchTrack(query: String) {
        if (query.isBlank()) {
            android.util.Log.d("SearchViewModel", "Query is blank, showing initial state")
            _searchScreenState.value = SearchState.Initial
            return
        }

        lastSearchQuery = query
        searchJob?.cancel()

        android.util.Log.d("SearchViewModel", "Starting search for: '$query'")
        _searchScreenState.value = SearchState.Searching

        searchJob = viewModelScope.launch {
            delay(500)

            try {
                android.util.Log.d("SearchViewModel", "Executing repository search")
                val tracks = searchRepository.searchTracks(query)
                android.util.Log.d("SearchViewModel", "Search completed. Found ${tracks.size} tracks")

                _searchScreenState.value = SearchState.Success(tracks)
            } catch (e: Exception) {
                android.util.Log.e("SearchViewModel", "Search error: ${e.message}", e)
                _searchScreenState.value = SearchState.Fail(e.message ?: "Unknown error")
            }
        }
    }

    fun retryLastSearch() {
        if (lastSearchQuery.isNotEmpty()) {
            searchTrack(lastSearchQuery)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        lastSearchQuery = ""
        _searchScreenState.value = SearchState.Initial
    }

    fun reset() {}

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = Creator.getTracksRepository()
                    return SearchViewModel(repository) as T
                }
            }
        }
    }
}