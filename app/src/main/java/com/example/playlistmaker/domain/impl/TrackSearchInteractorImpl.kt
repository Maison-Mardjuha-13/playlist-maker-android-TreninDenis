package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackSearchInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackSearchInteractorImpl(private val repository: TrackRepository) : TrackSearchInteractor {

    override suspend fun searchTracks(expression: String): List<Track> {
        return repository.searchTracks(expression)
    }
}