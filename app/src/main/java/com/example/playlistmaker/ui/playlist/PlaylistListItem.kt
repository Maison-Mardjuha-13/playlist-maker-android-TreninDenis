package com.example.playlistmaker.ui.playlist

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val Tracks = stringResource(R.string.Tracks)

    val medium = dimensionResource(R.dimen.medium) //16dp
    val xl = dimensionResource(R.dimen.xl) //48.dp

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = medium, horizontal = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(xl),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverImageUri != null && playlist.coverImageUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(playlist.coverImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.width(medium))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = m_text,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = playlist.description,
                fontSize = m_text,
                color = Color.Gray,
                maxLines = 1
            )
            Text(
                text = "$Tracks ${playlist.tracks.size}",
                fontSize = m_text,
                color = Color.Gray
            )
        }
    }
}