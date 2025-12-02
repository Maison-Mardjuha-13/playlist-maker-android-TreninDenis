package com.example.playlistmaker.ui.search

import SearchViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.viewmodel.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchText) {
        if (searchText.isEmpty()) {
            viewModel.clearSearch()
            showHistory = true
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText

                    coroutineScope.launch {
                        delay(1500)
                        if (searchText == newText) {
                            viewModel.performSearch(newText)
                            showHistory = false
                        }
                    }
                },
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
                                viewModel.clearSearch()
                                showHistory = true
                                keyboardController?.hide()
                            }
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotBlank()) {
                            viewModel.performSearch(searchText)
                            showHistory = false
                            keyboardController?.hide()
                        }
                    }
                )
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            showHistory && searchHistory.isNotEmpty() -> {
                SearchHistoryState(
                    history = searchHistory,
                    onHistoryItemClick = { query ->
                        searchText = query
                        viewModel.performSearchFromHistory(query)
                        showHistory = false
                        keyboardController?.hide()
                    },
                    onClearHistory = {
                        viewModel.clearSearchHistory()
                    }
                )
            }
            showHistory && searchHistory.isEmpty() -> {
                InitialState()
            }
            else -> {
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

@Composable
private fun SearchHistoryState(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Clear all",
                color = Color.Blue,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onClearHistory)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history) { query ->
                HistoryItem(
                    query = query,
                    onClick = { onHistoryItemClick(query) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun HistoryItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "History",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = query,
            fontSize = 16.sp
        )
    }
}

