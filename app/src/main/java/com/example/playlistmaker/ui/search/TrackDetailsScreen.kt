package com.example.playlistmaker.ui.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackId: Long,
    onBackClick: () -> Unit
) {
    val playlistViewModel: PlaylistViewModel = viewModel()
    var track by remember { mutableStateOf<Track?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    var showPlaylistSheet by remember { mutableStateOf(false) }

    LaunchedEffect(trackId) {
        try {
            track = playlistViewModel.getTrackById(trackId)
        } catch (e: Exception) {
            track = null
        } finally {
            isLoading = false
        }
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
                text = "Track Details",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (track == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Track not found", color = Color.Red)
            }
        } else {
            val currentTrack = track!!
            Column {
                Text("Title: ${currentTrack.trackName}", fontSize = 18.sp)
                Text("Artist: ${currentTrack.artistName}", fontSize = 16.sp)
                Text("Duration: ${currentTrack.getFormattedTrackTime()}", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            coroutineScope.launch {
                                val newFavoriteStatus = !currentTrack.isFavorite
                                playlistViewModel.toggleFavorite(currentTrack, newFavoriteStatus)
                                track = currentTrack.copy(isFavorite = newFavoriteStatus)
                            }
                        },
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    tint = if (currentTrack.isFavorite) Color.Red else Color.Gray
                )
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            showPlaylistSheet = true
                        },
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add to playlist",
                    tint = Color.Blue
                )
            }
        }
    }

    if (showPlaylistSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetState
        ) {
            PlaylistSelectionSheetContent(
                playlistViewModel = playlistViewModel,
                onPlaylistSelected = { playlistId ->
                    coroutineScope.launch {
                        track?.let {
                            playlistViewModel.insertTrackToPlaylist(it, playlistId)
                        }
                    }
                    showPlaylistSheet = false
                },
                onDismiss = { showPlaylistSheet = false }
            )
        }
    }
}

@Composable
fun PlaylistSelectionSheetContent(
    playlistViewModel: PlaylistViewModel,
    onPlaylistSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(playlists) {
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Playlist",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (playlists.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No playlists yet",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Create a playlist first",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistSelectionItem(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
fun PlaylistSelectionItem(
    playlist: com.example.playlistmaker.domain.models.Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${playlist.tracks.size} tracks",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}