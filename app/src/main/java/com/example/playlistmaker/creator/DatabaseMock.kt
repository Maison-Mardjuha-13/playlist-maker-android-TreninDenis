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
    val playlistsFlow: Flow<List<Playlist>> = _playlistsFlow

    init {
        addTestTracks()
        updatePlaylistsFlow()
    }

    private fun addTestTracks() {
        tracks.clear()
        tracks.addAll(
            listOf(
                Track(
                    id = 1,
                    trackName = "Bohemian Rhapsody",
                    artistName = "Queen",
                    trackTime = "5:55",
                    favorite = false,
                    playlistId = 0
                ),
                Track(
                    id = 2,
                    trackName = "Hotel California",
                    artistName = "Eagles",
                    trackTime = "6:30",
                    favorite = true,
                    playlistId = 0
                ),
                Track(
                    id = 3,
                    trackName = "Sweet Child O' Mine",
                    artistName = "Guns N' Roses",
                    trackTime = "5:03",
                    favorite = false,
                    playlistId = 0
                ),
                Track(
                    id = 4,
                    trackName = "Smells Like Teen Spirit",
                    artistName = "Nirvana",
                    trackTime = "5:01",
                    favorite = true,
                    playlistId = 0
                ),
                Track(
                    id = 5,
                    trackName = "Imagine",
                    artistName = "John Lennon",
                    trackTime = "3:07",
                    favorite = false,
                    playlistId = 0
                )
            )
        )
        println("DatabaseMock: Added ${tracks.size} test tracks")
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
        println("DatabaseMock: Updated playlists flow with ${filteredPlaylists.size} playlists")
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
        println("DatabaseMock: Added playlist '$name' with id $newId. Total playlists: ${playlists.size}")
        updatePlaylistsFlow()
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
        updatePlaylistsFlow()
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        tracks.removeIf { it.id == trackId }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        emit(tracks.find { it.trackName == track.trackName && it.artistName == track.artistName })
    }

    fun insertTrack(track: Track) {
        tracks.removeIf { it.id == track.id }
        tracks.add(track)
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300) // Имитируем задержку
        val favorites = tracks.filter { it.favorite }
        emit(favorites)
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.removeIf { it.playlistId == playlistId }
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
}