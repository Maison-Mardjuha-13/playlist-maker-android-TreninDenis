package com.example.playlistmaker.creator

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class DatabaseMock(
    private val scope: CoroutineScope,
) {
    private val historyList = mutableListOf<String>()
    private val _historyUpdates = MutableSharedFlow<Unit>()
    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()
    private val _playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())
    private val _favoritesFlow = MutableStateFlow<List<Track>>(emptyList())

    init {
        addTestTracks()
        updatePlaylistsFlow()
        updateFavoritesFlow()
    }

    private fun addTestTracks() {
        tracks.clear()
        tracks.addAll(
            listOf(
                Track(
                    id = 1,
                    trackName = "Bohemian Rhapsody",
                    artistName = "Queen",
                    collectionName = "Greatest Hits",
                    trackTimeMillis = 355000,
                    artworkUrl100 = null,
                    previewUrl = null,
                    isFavorite = false,
                    playlistId = null
                ),
                Track(
                    id = 2,
                    trackName = "Hotel California",
                    artistName = "Eagles",
                    collectionName = "Hotel California",
                    trackTimeMillis = 390000,
                    artworkUrl100 = null,
                    previewUrl = null,
                    isFavorite = true,
                    playlistId = null
                ),
                Track(
                    id = 3,
                    trackName = "Sweet Child O' Mine",
                    artistName = "Guns N' Roses",
                    collectionName = "Appetite for Destruction",
                    trackTimeMillis = 303000,
                    artworkUrl100 = null,
                    previewUrl = null,
                    isFavorite = false,
                    playlistId = null
                ),
                Track(
                    id = 4,
                    trackName = "Smells Like Teen Spirit",
                    artistName = "Nirvana",
                    collectionName = "Nevermind",
                    trackTimeMillis = 301000,
                    artworkUrl100 = null,
                    previewUrl = null,
                    playlistId = null
                ),
                Track(
                    id = 5,
                    trackName = "Imagine",
                    artistName = "John Lennon",
                    collectionName = "Imagine",
                    trackTimeMillis = 187000,
                    artworkUrl100 = null,
                    previewUrl = null,
                    isFavorite = false,
                    playlistId = null
                )
            )
        )
    }

    private fun updatePlaylistsFlow() {
        val filteredPlaylists = mutableListOf<Playlist>()
        playlists.forEach { playlist ->
            val playlistTracks = tracks.filter { track ->
                track.playlistId == playlist.id
            }
            filteredPlaylists.add(playlist.copy(tracks = playlistTracks))
        }
        _playlistsFlow.value = filteredPlaylists
    }

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
        notifyHistoryChanged()
    }

    private fun notifyHistoryChanged() {
        scope.launch(Dispatchers.IO) {
            _historyUpdates.emit(Unit)
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = _playlistsFlow

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        emit(playlists.find { it.id == id })
    }

    fun addNewPlaylist(name: String, description: String) {
        val newId = if (playlists.isEmpty()) 1L else playlists.maxOf { it.id } + 1
        playlists.add(
            Playlist(
                id = newId,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
        updatePlaylistsFlow()
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
        updatePlaylistsFlow()
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        val track = tracks.find { it.id == trackId }
        track?.let {
            tracks.remove(it)
            tracks.add(it.copy(playlistId = null))
        }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        emit(tracks.find { it.trackName == track.trackName && it.artistName == track.artistName })
    }

    fun insertTrack(track: Track) {
        tracks.removeIf { it.id == track.id }
        tracks.add(track)
        updateFavoritesFlow()
        updatePlaylistsFlow()
    }

    private fun updateFavoritesFlow() {
        val favorites = tracks.filter { it.isFavorite }
        _favoritesFlow.value = favorites
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300) // Имитируем задержку
        val favorites = tracks.filter { it.isFavorite }
        emit(favorites)
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.removeIf { it.playlistId == playlistId }
        updatePlaylistsFlow()
    }

    fun searchTracks(expression: String): List<Track> {
        if (expression.isEmpty()) return emptyList()

        return tracks.filter {
            it.trackName.contains(expression, true) ||
                    it.artistName.contains(expression, true)
        }
    }

    fun getTrackById(trackId: Long): Track? {
        return tracks.find { it.id == trackId }
    }

    fun deleteTrack(track: Track) {
        tracks.removeIf { it.id == track.id }
        updateFavoritesFlow()
        updatePlaylistsFlow()
    }
}