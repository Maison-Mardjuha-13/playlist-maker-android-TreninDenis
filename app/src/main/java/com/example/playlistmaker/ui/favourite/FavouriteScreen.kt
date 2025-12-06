package com.example.playlistmaker.ui.favourite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.TrackListItem
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavouriteScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit,
    playlistViewModel: PlaylistViewModel
) {
    val favoriteTracks by playlistViewModel.favoriteList.collectAsState(emptyList())
    val medium_size = dimensionResource(R.dimen.medium)
    val large_size = dimensionResource(R.dimen.large)
    val exlarge_size = dimensionResource(R.dimen.exlarge)
    val largetext_size = dimensionResource(R.dimen.largetext)
    val fav_title = stringResource(R.string.favourite_name)
    val no_fav = stringResource(R.string.no_favourite)
    val ask_del_from_fav = stringResource(R.string.ask_del_from_fav)
    val sure_del_fav = stringResource(R.string.sure_del_fav)
    val delete = stringResource(R.string.delete)
    val cancel = stringResource(R.string.cancel)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(top = medium_size, start = medium_size, end = medium_size)
            .fillMaxWidth()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = large_size),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(exlarge_size)
                    .clickable {
                        onBackClick()
                    },
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(medium_size))
            Text(
                text = fav_title,
                fontSize = largetext_size.value.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (favoriteTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(medium_size),
                contentAlignment = Alignment.Center
            ) {
                Text(no_fav, color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(favoriteTracks.size) { index ->
                    val track = favoriteTracks[index]
                    TrackListItem(
                        track = track,
                        onClick = { onTrackClick(track.id) },
                        onLongClick = {
                            trackToDelete = track
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && trackToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                trackToDelete = null
            },
            title = {
                Text(
                    text = ask_del_from_fav,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = sure_del_fav
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            trackToDelete?.let { track ->
                                playlistViewModel.toggleFavorite(track, false)
                            }
                            showDeleteDialog = false
                            trackToDelete = null
                        }
                    }
                ) {
                    Text(delete, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        trackToDelete = null
                    }
                ) {
                    Text(cancel)
                }
            }
        )
    }
}