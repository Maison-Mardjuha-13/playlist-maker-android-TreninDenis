package com.example.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {
    private val playlistsRepository: PlaylistsRepository = Creator.getPlaylistsRepository()
    private val tracksRepository: TracksRepository = Creator.getTracksRepository()

    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks().flowOn(Dispatchers.IO)

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists().flowOn(Dispatchers.IO)
    private var _coverImageUri = MutableStateFlow<String?>(null)
    val coverImageUri: StateFlow<String?> = _coverImageUri.asStateFlow()


    init {
        println("PlaylistViewModel: Initializing and loading playlists")
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playlistsRepository.getAllPlaylists().collect { playlistsList ->
                    println("ViewModel: Received ${playlistsList.size} playlists from repository")
                    _playlists.value = playlistsList
                }
            } catch (e: Exception) {
                println("PlaylistViewModel: Error loading playlists: ${e.message}")
            }
        }
    }

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCoverUri = _coverImageUri.value
            playlistsRepository.addNewPlaylist(namePlaylist, description, currentCoverUri)
            _coverImageUri.value = null
            loadPlaylists()
        }
    }

    fun setCoverImageUri(uri: String?) {
        _coverImageUri.value = uri
    }

    fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlists.map { playlistsList ->
            playlistsList.find { it.id == playlistId }
        }
    }

    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> {
        return tracksRepository.getTracksByPlaylistId(playlistId)
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun toggleFavorite(track: Track, isFavorite: Boolean) {
        tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistsRepository.deletePlaylistById(playlistId)
        loadPlaylists()
    }

    suspend fun getTrackById(trackId: Long): Track? {
        return tracksRepository.getTrackById(trackId)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = PlaylistViewModelFactory()
    }
}