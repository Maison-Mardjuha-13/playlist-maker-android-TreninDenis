package com.example.playlistmaker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.database.dao.PlaylistDao
import com.example.playlistmaker.data.database.dao.TracksDao
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.TrackEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun playlistDao(): PlaylistDao
}

fun TrackEntity.toTrack(): Track {
    return Track(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        collectionName = this.collectionName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        previewUrl = this.previewUrl,
        isFavorite = this.isFavorite,
        playlistId = this.playlistId
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        collectionName = this.collectionName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        previewUrl = this.previewUrl,
        isFavorite = this.isFavorite,
        playlistId = this.playlistId
    )
}

fun PlaylistEntity.toPlaylist(tracks: List<Track> = emptyList()): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        tracks = tracks
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description
    )
}