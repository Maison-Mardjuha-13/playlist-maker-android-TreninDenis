package com.example.playlistmaker.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.viewmodel.SearchState
import com.example.playlistmaker.ui.viewmodel.SearchViewModel


@Composable
fun SearchScreen(
    modifier: Modifier,
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit
) {
    val xs = dimensionResource(R.dimen.xs)
    val medium_size = dimensionResource(com.example.playlistmaker.R.dimen.medium)
    val large_size = dimensionResource(com.example.playlistmaker.R.dimen.large)
    val exlarge_size = dimensionResource(com.example.playlistmaker.R.dimen.exlarge)
    val largetext_size = dimensionResource(com.example.playlistmaker.R.dimen.largetext)
    val s_title = stringResource(R.string.search_name)
    val err = stringResource(R.string.err_text)
    val ent = stringResource(R.string.enter_string)


    val screenState by viewModel.searchScreenState.collectAsState()
    var text by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            viewModel.searchTrack(text)
        }
        viewModel.reset()
    }

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
                text = s_title,
                fontSize = largetext_size.value.sp,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
            },
            placeholder = {Text(s_title)},
            leadingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        viewModel.searchTrack(text)
                    },
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (text.isNotEmpty()){
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {text = ""}
                    )
                }

            },
            modifier = Modifier.fillMaxWidth()
        )

        when (screenState) {
            is SearchState.Initial -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(ent)
                }
            }

            is SearchState.Searching -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                val tracks = (screenState as SearchState.Success).foundList
                LazyColumn(
                    modifier = modifier.fillMaxSize()
                ) {
                    items(tracks.size) { index ->
                        TrackListItem(
                            track = tracks[index],
                            onClick = { onTrackClick(tracks[index].id) }
                        )
                    }
                }
            }

            is SearchState.Fail -> {
                val error = (screenState as SearchState.Fail).error
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$err $error", color = Color.Red)
                }
            }
        }
    }
}