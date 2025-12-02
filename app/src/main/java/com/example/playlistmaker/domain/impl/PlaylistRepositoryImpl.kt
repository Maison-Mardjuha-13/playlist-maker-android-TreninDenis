package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.data.database.dao.PlaylistDao
import com.example.playlistmaker.data.database.dao.TracksDao
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.toPlaylist
import com.example.playlistmaker.data.database.toTrack
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val scope: CoroutineScope,
    private val playlistDao: PlaylistDao,
    private val tracksDao: TracksDao
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId)
            .combine(tracksDao.getTracksByPlaylistId(playlistId)) { playlistEntity, trackEntities ->
                playlistEntity?.toPlaylist(trackEntities.map { it.toTrack() })
            }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { playlistEntity ->
                    playlistEntity.toPlaylist()
                }
            }
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        playlistDao.insertPlaylist(
            com.example.playlistmaker.data.database.entity.PlaylistEntity(
                name = name,
                description = description
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        tracksDao.deleteTracksByPlaylistId(id)
        playlistDao.deletePlaylistById(id)
    }
}