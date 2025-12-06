package com.example.playlistmaker.ui.playlist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    playlistViewModel: PlaylistViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }
    val scope = rememberCoroutineScope()
    
    val del_pl = stringResource(R.string.ask_del_pl)
    val sure_del_pl = stringResource(R.string.sure_del_pl)
    val no_back = stringResource(R.string.no_step_to_back)
    val delete = stringResource(R.string.delete)
    val cancel = stringResource(R.string.cancel)
    val pl_title = stringResource(R.string.playlist_name)
    val no_pl_yet = stringResource(R.string.no_pl_yet)

    val xs = dimensionResource(R.dimen.xs) // 1dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp
    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp
    val xl_text = dimensionResource(R.dimen.xltext).value.sp //32sp

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            playlistViewModel.setCoverImageUri(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    if (showDeleteDialog && playlistToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                playlistToDelete = null
            },
            title = {
                Text(
                    text = del_pl,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "$sure_del_pl \"${playlistToDelete?.name}\"? " + no_back
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            playlistToDelete?.id?.let { playlistId ->
                                playlistViewModel.deletePlaylist(playlistId)
                            }
                            showDeleteDialog = false
                            playlistToDelete = null
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
                        playlistToDelete = null
                    }
                ) {
                    Text(cancel)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        .clickable { navigateBack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
                Text(
                    text = pl_title,
                    fontSize = xl_text,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = medium)
                )
            }

            Spacer(modifier = Modifier.height(medium))

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = no_pl_yet,
                        color = Color.Gray,
                        fontSize = m_text
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(playlists.size) { index ->
                        val playlist = playlists[index]
                        PlaylistListItem(
                            playlist = playlist,
                            onClick = {
                                navigateToPlaylist(playlist.id)
                            },
                            onLongClick = {
                                playlistToDelete = playlist
                                showDeleteDialog = true
                            }
                        )
                        HorizontalDivider(thickness = xs)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                addNewPlaylist()
            },
            modifier = Modifier
                .padding(medium)
                .align(Alignment.BottomEnd),
            containerColor = Color(0xFF4CAF50)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

