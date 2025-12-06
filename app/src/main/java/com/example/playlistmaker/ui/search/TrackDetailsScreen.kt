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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlistmaker.R
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

    val medium = dimensionResource(R.dimen.medium) //16dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp
    val xl = dimensionResource(R.dimen.xl) //48.dp


    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp
    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp

    val track_details = stringResource(R.string.track_details)
    val track_not_found = stringResource(R.string.track_not_found)
    val trackName = stringResource(R.string.trackName)
    val artistName = stringResource(R.string.artistName)
    val trDuration = stringResource(R.string.duration)


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
            .padding(medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(exlarge)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Text(
                text = track_details,
                fontSize = large_text,
                modifier = Modifier.padding(start = medium)
            )
        }

        Spacer(modifier = Modifier.height(exlarge))

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
                Text(track_not_found, color = Color.Red)
            }
        } else {
            val currentTrack = track!!
            Column {
                Text("$trackName ${currentTrack.trackName}", fontSize = m_text)
                Text("$artistName ${currentTrack.artistName}", fontSize = m_text)
                Text("$trDuration ${currentTrack.getFormattedTrackTime()}", fontSize = m_text)
            }

            Spacer(modifier = Modifier.height(exlarge))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    modifier = Modifier
                        .size(xl)
                        .clickable {
                            coroutineScope.launch {
                                val newFavoriteStatus = !currentTrack.isFavorite
                                playlistViewModel.toggleFavorite(currentTrack, newFavoriteStatus)
                                track = currentTrack.copy(isFavorite = newFavoriteStatus)
                            }
                        },
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = if (currentTrack.isFavorite) Color.Red else Color.Gray
                )
                Icon(
                    modifier = Modifier
                        .size(xl)
                        .clickable {
                            showPlaylistSheet = true
                        },
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
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

    val small = dimensionResource(R.dimen.small) //8dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val large = dimensionResource(R.dimen.large) //24dp
    val xxxl = dimensionResource(R.dimen.xxxl) //100dp
    val s400 = dimensionResource(R.dimen.s400)


    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp

    val select_pl = stringResource(R.string.select_pl)
    val no_pl_yet = stringResource(R.string.no_pl_yet)
    val create_pl_first = stringResource(R.string.create_pl_first)
    val cancel = stringResource(R.string.cancel)

    LaunchedEffect(playlists) {
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(medium)
    ) {
        Text(
            text = select_pl,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = medium)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xxxl),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (playlists.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = no_pl_yet,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = small)
                )
                Text(
                    text = create_pl_first,
                    color = Color.LightGray,
                    fontSize = m_text
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = s400)
            ) {
                items(playlists) { playlist ->
                    PlaylistSelectionItem(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(medium))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(cancel)
        }
    }
}

@Composable
fun PlaylistSelectionItem(
    playlist: com.example.playlistmaker.domain.models.Playlist,
    onClick: () -> Unit
) {
    val tracks = stringResource(R.string.tracks)

    val very_small = dimensionResource(R.dimen.very_small)
    val medium = dimensionResource(R.dimen.medium)
    val xs = dimensionResource(R.dimen.xs)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = very_small),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(medium)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = very_small)
            )
            Text(
                text = "${playlist.tracks.size} $tracks",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                modifier = Modifier.padding(top = very_small)
            )
        }
    }
}