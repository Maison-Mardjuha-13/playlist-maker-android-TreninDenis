package com.example.playlistmaker.ui.playlist

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel

@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    playlistViewModel: PlaylistViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit,
    navController: NavHostController
) {
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    val coverImageUri by playlistViewModel.coverImageUri.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        println("PlaylistsScreen: Starting to observe playlists")
    }

    LaunchedEffect(playlists) {
        println("PlaylistsScreen: Current playlists count: ${playlists.size}")
        playlists.forEach { playlist ->
            println("Playlist: ${playlist.id} - ${playlist.name}")
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
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
                        .clickable { navigateBack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
                Text("Плейлисты", fontSize = 32.sp, modifier = Modifier.padding(start = 16.dp))
            }


            Spacer(modifier = Modifier.height(16.dp))

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No playlists yet", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(playlists.size) { index ->
                        val playlist = playlists[index]
                        PlaylistContent(playlist = playlist) {
                            println("PlaylistsScreen: Navigating to playlist ${playlist.id} - ${playlist.name}")
                            navigateToPlaylist(playlist.id)
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                println("PlaylistsScreen: FAB clicked - navigating to new playlist")
                addNewPlaylist()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color.Gray
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add playlist"
            )
        }
    }
}





@Composable
fun PlaylistContent(playlist: Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverImageUri != null && playlist.coverImageUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(playlist.coverImageUri),
                    contentDescription = "Playlist cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = "Default playlist icon",
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = playlist.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Треков: ${playlist.tracks.size}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}