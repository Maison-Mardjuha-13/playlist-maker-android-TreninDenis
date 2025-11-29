package com.example.playlistmaker.domain.models

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackId")
    val id: Long,

    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("collectionName")
    val collectionName: String?,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long?,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String?,

    @SerializedName("previewUrl")
    val previewUrl: String?,

    val isFavorite: Boolean = false,
    val playlistId: Long? = null
) {
    val trackTime: String
        get() = getFormattedTrackTime()

    val favorite: Boolean
        get() = isFavorite

    fun getFormattedTrackTime(): String {
        return trackTimeMillis?.let { timeMillis ->
            val totalSeconds = timeMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        } ?: "--:--"
    }
}

data class ITunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
)
