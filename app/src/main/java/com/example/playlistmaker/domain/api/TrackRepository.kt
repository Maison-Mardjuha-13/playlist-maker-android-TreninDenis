package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
    suspend fun getTrackByNameAndArtist(track: Track): Track?
    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun deleteTrackFromPlaylist(track: Track)
    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getTrackById(trackId: Long): Track?
    fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>>

}