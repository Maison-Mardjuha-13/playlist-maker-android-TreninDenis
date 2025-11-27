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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.TrackListItem
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel

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
    var fav_title = stringResource(R.string.favourite_name)

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
                imageVector = Icons.Default.ArrowBack,
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
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет избранных треков", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(favoriteTracks.size) { index ->
                    TrackListItem(
                        track = favoriteTracks[index],
                        onClick = { onTrackClick(favoriteTracks[index].id) }
                    )
                }
            }
        }
    }
}