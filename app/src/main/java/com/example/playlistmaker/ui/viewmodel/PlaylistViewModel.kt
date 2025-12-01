package com.example.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {
    private val playlistsRepository: PlaylistsRepository = Creator.getPlaylistsRepository()
    private val tracksRepository: TracksRepository = Creator.getTracksRepository()


    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: Flow<List<Playlist>> = _playlists.asStateFlow()


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

                    (playlistsRepository as? PlaylistsRepositoryImpl)?.let { repo ->
                        repo.debugDatabase()
                    }
                }
            } catch (e: Exception) {
                println("PlaylistViewModel: Error loading playlists: ${e.message}")
            }
        }
    }

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description)
            loadPlaylists()
        }
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

    //suspend fun deletePlaylistById(id: Long) {
    //    tracksRepository.deleteTracksByPlaylistId(id)
    //    playlistsRepository.deletePlaylistById(id)
    //}

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track).firstOrNull()
    }

    suspend fun getTrackById(trackId: Long): Track? {
        return tracksRepository.getTrackById(trackId)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = PlaylistViewModelFactory()
    }
}