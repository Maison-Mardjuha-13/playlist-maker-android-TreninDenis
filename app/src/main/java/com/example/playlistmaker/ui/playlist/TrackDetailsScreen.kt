package com.example.playlistmaker.ui.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun TrackDetailsScreen(
    trackId: Long,
    onBackClick: () -> Unit
) {
    val playlistViewModel: PlaylistViewModel = viewModel()
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var track by remember { mutableStateOf<Track?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(trackId) {
        coroutineScope.launch {
            try {
                track = playlistViewModel.getTrackById(trackId)
            } catch (e: Exception) {
                println("Error loading track: ${e.message}")
            } finally {
                isLoading = false
            }
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
                contentDescription = null
            )
            Text(
                text = "Детали трека",
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
                Text("Трек не найден", color = Color.Red)
            }
        } else {
            // Отображаем реальные данные трека
            val currentTrack = track!!
            Column {
                Text("Название: ${currentTrack.trackName}", fontSize = 18.sp)
                Text("Исполнитель: ${currentTrack.artistName}", fontSize = 16.sp)
                Text("Время: ${currentTrack.trackTime}", fontSize = 16.sp)

                // Отображаем статус избранного
                Text(
                    text = "В избранном: ${if (currentTrack.favorite) "Да" else "Нет"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
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
                            // Добавить/удалить из избранного
                            coroutineScope.launch {
                                track?.let {
                                    playlistViewModel.toggleFavorite(it, !it.favorite)
                                    // Обновляем локальное состояние
                                    track = it.copy(favorite = !it.favorite)
                                }
                            }
                        },
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "В избранное",
                    tint = if (track?.favorite == true) Color.Red else Color.Gray
                )
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            showPlaylistSheet = true
                        },
                    imageVector = Icons.Filled.Add,
                    contentDescription = "В плейлист",
                    tint = Color.Blue
                )
            }
        }

        if (showPlaylistSheet) {
            PlaylistSelectionSheet(
                onDismiss = { showPlaylistSheet = false },
                onPlaylistSelected = { playlistId ->
                    // Добавить трек в плейлист
                    coroutineScope.launch {
                        track?.let {
                            playlistViewModel.insertTrackToPlaylist(it, playlistId)
                        }
                    }
                    showPlaylistSheet = false
                }
            )
        }
    }
}

@Composable
fun PlaylistSelectionSheet(
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Выбор плейлиста (реализуется позже)")
    }
}