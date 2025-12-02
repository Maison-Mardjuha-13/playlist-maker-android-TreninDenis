package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.database.dao.TracksDao
import com.example.playlistmaker.data.database.toEntity
import com.example.playlistmaker.data.database.toTrack
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.data.network.ITunesApi
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val scope: CoroutineScope,
    private val iTunesApi: ITunesApi,
    private val tracksDao: TracksDao
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        return try {
            if (expression.isBlank()) {
                return emptyList()
            }

            val response = iTunesApi.search(expression)

            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()

                results.forEach { track ->
                    tracksDao.insertTrack(track.toEntity())
                }

                results
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { it?.toTrack() }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksDao.insertTrack(track.copy(playlistId = playlistId).toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksDao.deleteTrackById(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        tracksDao.updateFavoriteStatus(track.id, isFavorite)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        tracksDao.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksDao.getFavoriteTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun getTrackById(trackId: Long): Track? {
        return tracksDao.getTrackById(trackId)?.toTrack()
    }

    override fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>> {
        return tracksDao.getTracksByPlaylistId(playlistId)
            .map { entities -> entities.map { it.toTrack() } }
    }
}