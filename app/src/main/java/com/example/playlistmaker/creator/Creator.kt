package com.example.playlistmaker.creator


import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.database.AppDatabase
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.NetworkModule
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Creator {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var database: AppDatabase? = null

    fun initDatabase(context: Context) {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "playlist_maker.db"
        ).build()
    }

    private fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Database not initialized. Call initDatabase first.")
    }

    fun getTracksRepository(): TracksRepository {
        val retrofit = RetrofitNetworkClient().getRetrofit()
        val iTunesApi = retrofit.create(ITunesApi::class.java)
        return TracksRepositoryImpl(scope, iTunesApi, getDatabase().tracksDao())
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(
            scope,
            getDatabase().playlistDao(),
            getDatabase().tracksDao()
        )
    }
}