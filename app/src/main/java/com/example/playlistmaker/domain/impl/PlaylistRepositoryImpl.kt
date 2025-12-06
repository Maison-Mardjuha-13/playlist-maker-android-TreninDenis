package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.database.dao.PlaylistDao
import com.example.playlistmaker.data.database.dao.TracksDao
import com.example.playlistmaker.data.database.toPlaylist
import com.example.playlistmaker.data.database.toTrack
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val scope: CoroutineScope,
    private val playlistDao: PlaylistDao,
    private val tracksDao: TracksDao
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId)
            .map { playlistEntity ->
                playlistEntity?.let {
                    val tracks = tracksDao.getTracksForPlaylist(playlistId)
                    it.toPlaylist(tracks.map { trackEntity -> trackEntity.toTrack() })
                }
            }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { playlistEntity ->
                    val tracks = tracksDao.getTracksForPlaylist(playlistEntity.id)
                    playlistEntity.toPlaylist(tracks.map { trackEntity -> trackEntity.toTrack() })
                }
            }
    }

    override suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverImageUri: String?
    ) {
        playlistDao.insertPlaylist(
            com.example.playlistmaker.data.database.entity.PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = coverImageUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        tracksDao.deleteTracksByPlaylistId(id)
        playlistDao.deletePlaylistById(id)
    }
}