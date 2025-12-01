package com.example.playlistmaker.ui.search

import SearchViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.viewmodel.SearchState


@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            viewModel.searchTrack(searchText)
        } else {
            viewModel.clearSearch()
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Search",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Enter search query") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Icon(
                        Icons.Default.Clear,
                        "Clear",
                        modifier = Modifier.clickable {
                            searchText = ""
                            keyboardController?.hide()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (screenState) {
            SearchState.Initial -> {
                InitialState()
            }
            SearchState.Searching -> {
                SearchingState()
            }
            is SearchState.Success -> {
                val tracks = (screenState as SearchState.Success).foundList
                if (tracks.isEmpty()) {
                    EmptyResultsState()
                } else {
                    SuccessState(tracks, onTrackClick)
                }
            }
            is SearchState.Fail -> {
                ErrorState(
                    error = (screenState as SearchState.Fail).error,
                    onRetry = { viewModel.retryLastSearch() }
                )
            }
        }
    }
}

@Composable
private fun InitialState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Enter search query")
    }
}

@Composable
private fun SearchingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyResultsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_search_emply),
                contentDescription = "No results",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nothing found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try changing your search query",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = "Error",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Server error",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check your internet connection and try again",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Retry",
                color = Color.Blue,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onRetry)
            )
        }
    }
}

@Composable
private fun SuccessState(tracks: List<Track>, onTrackClick: (Long) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackListItem(
                track = track,
                onClick = { onTrackClick(track.id) }
            )
            HorizontalDivider()
        }
    }
}

