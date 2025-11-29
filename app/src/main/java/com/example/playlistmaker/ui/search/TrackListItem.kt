package com.example.playlistmaker.ui.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.ic_music),
            contentDescription = "Track",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontSize = 16.sp
            )
            Text(
                text = track.artistName,
                color = Color.Gray,
                maxLines = 1,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = track.getFormattedTrackTime(),
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}