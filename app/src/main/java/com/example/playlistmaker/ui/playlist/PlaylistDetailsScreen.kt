package com.example.playlistmaker.ui.playlist

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.search.TrackListItem
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.R

@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel,
    coverImageUri: String?
) {
    var playlist by remember { mutableStateOf<Playlist?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val playlistTracks by playlistViewModel.getPlaylistTracks(playlistId).collectAsState(emptyList())

    LaunchedEffect(playlistId) {
        try {
            playlistViewModel.playlists.collect { playlists ->
                val foundPlaylist = playlists.find { it.id == playlistId }
                playlist = foundPlaylist?.copy(tracks = playlistTracks)
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
        }
    }

    LaunchedEffect(playlistTracks) {
        playlist = playlist?.copy(tracks = playlistTracks)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
            Text(
                text = "Playlist Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (coverImageUri != null) {
                AsyncImage(
                    model = Uri.parse(coverImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_cover_photo_add),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (playlist == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Playlist not found", color = Color.Red)
            }
        } else {
            val currentPlaylist = playlist!!

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = currentPlaylist.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentPlaylist.description,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${currentPlaylist.tracks.size} tracks",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (currentPlaylist.tracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks in this playlist",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentPlaylist.tracks) { track ->
                        TrackListItem(
                            track = track,
                            onClick = { onTrackClick(track.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}