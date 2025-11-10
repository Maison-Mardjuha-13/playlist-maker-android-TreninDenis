package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.delay

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        delay(1000) // Эммулируем задержку ответа
        return if (response.resultCode == 200) { // успешный запрос
            (response as TrackSearchResponse).results.map {
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime = "%02d".format(minutes) + ":" + "%02d".format(seconds - minutes * 60)
                Track(it.trackName, it.artistName, trackTime) }
        } else {
            emptyList()
        }
    }
}