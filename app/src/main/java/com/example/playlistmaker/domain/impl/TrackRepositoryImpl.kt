package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.database.dao.TracksDao
import com.example.playlistmaker.data.database.entity.TrackEntity
import com.example.playlistmaker.data.database.toEntity
import com.example.playlistmaker.data.database.toTrack
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.data.network.ITunesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun getTrackByNameAndArtist(track: Track): Track? {
        val entity = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)
        return entity?.toTrack()
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksDao.insertTrack(track.copy(playlistId = playlistId).toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksDao.deleteTrackById(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        println("updateTrackFavoriteStatus: ${track.trackName}, favorite: $isFavorite")

        val existingTrack = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)

        if (existingTrack != null) {
            val updatedTrack = existingTrack.copy(isFavorite = isFavorite)

            tracksDao.update(updatedTrack)
            println("Updated existing track ID: ${existingTrack.id}")
        } else {
            val newTrack = track.toEntity().copy(isFavorite = isFavorite)
            tracksDao.insert(newTrack)
            println("Inserted new track")
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        tracksDao.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksDao.getFavoriteTracks()
            .map { trackEntities ->
                trackEntities.map { it.toTrack() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getTrackById(trackId: Long): Track? {
        return tracksDao.getTrackById(trackId)?.toTrack()
    }

    override fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>> {
        return tracksDao.getTracksByPlaylistId(playlistId)
            .map { entities -> entities.map { it.toTrack() } }
    }

}