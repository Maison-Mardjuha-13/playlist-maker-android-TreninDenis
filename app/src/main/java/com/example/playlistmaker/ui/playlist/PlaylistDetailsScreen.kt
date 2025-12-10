package com.example.playlistmaker.ui.playlist

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.TrackListItem
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel,
    coverImageUri: String?,
    onDeletePlaylist: () -> Unit
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    val playlistTracks by playlistViewModel.getPlaylistTracks(playlistId).collectAsState(emptyList())

    val playlist = remember(playlistId, playlists) {
        playlists.find { it.id == playlistId }?.copy(tracks = playlistTracks)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val small = dimensionResource(R.dimen.small) //8dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val large = dimensionResource(R.dimen.large) //24dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp
    val dvesti = dimensionResource(R.dimen.dvesti)

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp
    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp

    val pl_details = stringResource(R.string.pl_details)
    val pl_not_found = stringResource(R.string.pl_not_found)
    val tracks = stringResource(R.string.tracks)
    val no_tracks = stringResource(R.string.no_tracks_in_pl)
    val sure = stringResource(R.string.sure_del_pl)
    val no_back = stringResource(R.string.no_step_to_back)
    val ask_del_pl = stringResource(R.string.ask_del_pl)
    val delete = stringResource(R.string.delete)
    val cancel = stringResource(R.string.cancel)

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
                text = pl_details,
                fontSize = large_text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = medium)
                    .weight(1f)
            )
            Icon(
                modifier = Modifier
                    .size(exlarge)
                    .clickable { showDeleteDialog = true },
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.Red
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dvesti)
                .padding(top = medium)
        ) {
            val actualCoverUri = coverImageUri ?: playlist?.coverImageUri
            if (actualCoverUri != null && actualCoverUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(actualCoverUri),
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

        Spacer(modifier = Modifier.height(exlarge))

        if (playlist == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(pl_not_found, color = Color.Red)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = large)
            ) {
                Text(
                    text = playlist.name,
                    fontSize = large_text,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(small))
                Text(
                    text = playlist.description,
                    fontSize = m_text,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(small))
                Text(
                    text = "${playlist.tracks.size} $tracks",
                    fontSize = m_text,
                    color = Color.Gray
                )
            }

            if (playlist.tracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = no_tracks,
                        color = Color.Gray,
                        fontSize = m_text
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlist.tracks) { track ->
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(ask_del_pl) },
            text = {
                Text(
                    "$sure \"${playlist?.name}\"? " + no_back
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            playlistViewModel.deletePlaylist(playlistId)
                            onDeletePlaylist()
                        }
                    }
                ) {
                    Text(delete, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(cancel)
                }
            }
        )
    }
}