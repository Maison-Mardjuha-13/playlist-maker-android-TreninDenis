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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val scope: CoroutineScope,
    private val iTunesApi: ITunesApi,
    private val tracksDao: TracksDao
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        if (expression.isBlank()) return emptyList()

        return try {
            val response = iTunesApi.search(expression)

            if (response.isSuccessful) {
                val apiTracks = response.body()?.results ?: emptyList()

                val resultTracks = apiTracks.map { apiTrack ->
                    val dbTrack = tracksDao.getTrackByNameAndArtist(
                        apiTrack.trackName,
                        apiTrack.artistName
                    )

                    apiTrack.copy(
                        isFavorite = dbTrack?.isFavorite ?: false,
                        playlistId = dbTrack?.playlistId
                    )
                }
                saveOrUpdateTracks(apiTracks)

                resultTracks
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun saveOrUpdateTracks(tracks: List<Track>) {
        tracks.forEach { track ->
            val existing = tracksDao.getTrackByNameAndArtist(
                track.trackName,
                track.artistName
            )

            if (existing != null) {
                val updatedEntity = TrackEntity(
                    id = track.id,
                    trackName = track.trackName,
                    artistName = track.artistName,
                    collectionName = track.collectionName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    previewUrl = track.previewUrl,
                    isFavorite = existing.isFavorite,
                    playlistId = existing.playlistId
                )
                tracksDao.update(updatedEntity)
            } else {
                tracksDao.insertTrack(track.toEntity())
            }
        }
    }

    override suspend fun getTrackByNameAndArtist(track: Track): Track? {
        return tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            ?.toTrack()
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val existingTrack = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)

        if (existingTrack != null) {
            val updatedTrack = existingTrack.copy(playlistId = playlistId)
            tracksDao.update(updatedTrack)
        } else {
            val newTrack = track.toEntity().copy(playlistId = playlistId)
            tracksDao.insertTrack(newTrack)
        }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksDao.deleteTrackById(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val existing = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)

        if (existing != null) {
            tracksDao.update(existing.copy(isFavorite = isFavorite))
        } else {
            tracksDao.insertTrack(track.toEntity().copy(isFavorite = isFavorite))
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        tracksDao.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksDao.getFavoriteTracks()
            .map { entities -> entities.map { it.toTrack() } }
    }

    override suspend fun getTrackById(trackId: Long): Track? {
        return tracksDao.getTrackById(trackId)?.toTrack()
    }

    override fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>> {
        return tracksDao.getTracksByPlaylistId(playlistId)
            .map { entities -> entities.map { it.toTrack() } }
    }

}