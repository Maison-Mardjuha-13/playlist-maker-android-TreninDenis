package com.example.playlistmaker.creator


import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Creator {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val databaseMock = DatabaseMock(scope)

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(scope)
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(scope)
    }

    fun getDatabaseMock(): DatabaseMock {
        return databaseMock
    }
}