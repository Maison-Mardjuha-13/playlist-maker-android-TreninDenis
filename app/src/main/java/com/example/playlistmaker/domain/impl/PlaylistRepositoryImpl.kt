package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(
    private val scope: CoroutineScope
) : PlaylistsRepository {
    private val database = Creator.getDatabaseMock()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return database.getPlaylist(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return database.getAllPlaylists()
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        database.addNewPlaylist(
            name = name,
            description = description
        )
    }

    //override suspend fun deletePlaylistById(id: Long) {
    //    database.deletePlaylistById(playlistId = id)
    //}

    suspend fun debugDatabase() {
        database.debugPlaylists()
    }
}