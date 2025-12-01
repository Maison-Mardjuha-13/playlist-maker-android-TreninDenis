package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.data.network.ITunesApi

class TracksRepositoryImpl(
    private val scope: CoroutineScope,
    private val iTunesApi: ITunesApi
) : TracksRepository {
    private val database = Creator.getDatabaseMock()

    override suspend fun searchTracks(expression: String): List<Track> {
        return try {
            android.util.Log.d("TracksRepository", "Searching for: '$expression'")

            if (expression.isBlank()) {
                return emptyList()
            }

            val response = iTunesApi.search(expression)
            android.util.Log.d("TracksRepository", "Response code: ${response.code()}")
            android.util.Log.d("TracksRepository", "Response isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()
                android.util.Log.d("TracksRepository", "Found ${results.size} tracks")

                results.forEach { track ->
                    database.insertTrack(track)
                }

                results.forEachIndexed { index, track ->
                    android.util.Log.d("TracksRepository", "Track $index: ${track.trackName} by ${track.artistName}")
                }
                results
            } else {
                android.util.Log.e("TracksRepository", "API error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("TracksRepository", "Search error: ${e.message}", e)
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.deleteTrackFromPlaylist(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(isFavorite = isFavorite))
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun getTrackById(trackId: Long): Track? {
        return database.getTrackById(trackId)
    }
}