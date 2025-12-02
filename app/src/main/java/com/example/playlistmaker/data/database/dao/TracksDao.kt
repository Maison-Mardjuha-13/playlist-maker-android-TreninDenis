package com.example.playlistmaker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE trackName = :name AND artistName = :artist")
    fun getTrackByNameAndArtist(name: String, artist: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE isFavorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId")
    fun getTracksByPlaylistId(playlistId: Long): Flow<List<TrackEntity>>

    @Query("DELETE FROM tracks WHERE playlistId = :playlistId")
    suspend fun deleteTracksByPlaylistId(playlistId: Long)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("UPDATE tracks SET isFavorite = :isFavorite WHERE id = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId")
    suspend fun getTracksForPlaylist(playlistId: Long): List<TrackEntity>
}