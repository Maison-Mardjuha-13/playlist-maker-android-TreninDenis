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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    val medium = dimensionResource(R.dimen.medium) //16dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp

    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp

    val search_title = stringResource(R.string.search_name)
    val enter_string = stringResource(R.string.enter_string)

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
            .padding(medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(exlarge)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(medium))
            Text(
                text = search_title,
                fontSize = large_text,
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
                placeholder = { Text(enter_string) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear, contentDescription = null,
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

        Spacer(modifier = Modifier.height(medium))

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
    val enter_string = stringResource(R.string.enter_string)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(enter_string)
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
    val small = dimensionResource(R.dimen.small) //8dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val xxxl = dimensionResource(R.dimen.xxxl)

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp

    val change_search = stringResource(R.string.try_change_search)
    val nothing_found = stringResource(R.string.nothing_found)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_search_emply),
                contentDescription = null,
                modifier = Modifier.size(xxxl)
            )
            Spacer(modifier = Modifier.height(medium))
            Text(
                text = nothing_found,
                fontSize = m_text,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(small))
            Text(
                text = change_search,
                fontSize = m_text,
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
    val xs = dimensionResource(R.dimen.xs) //0.5dp
    val small = dimensionResource(R.dimen.small) //8dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val large = dimensionResource(R.dimen.large) //24dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp
    val xl = dimensionResource(R.dimen.xl) //48.dp
    val xxl = dimensionResource(R.dimen.xxl) //64dp
    val xxxl = dimensionResource(R.dimen.xxxl)
    val dvesti = dimensionResource(R.dimen.dvesti)

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp
    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp

    val server_error = stringResource(R.string.server_error)
    val check_inet = stringResource(R.string.check_inet)
    val retry = stringResource(R.string.retry)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.size(xxxl)
            )
            Spacer(modifier = Modifier.height(medium))
            Text(
                text = server_error,
                fontSize = m_text,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(small))
            Text(
                text = check_inet,
                fontSize = m_text,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(medium))
            Text(
                text = retry,
                color = Color.Blue,
                fontSize = m_text,
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
    val small = dimensionResource(R.dimen.small) //8dp

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp

    val search_history = stringResource(R.string.search_history)
    val clear_all = stringResource(R.string.clear_all)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = search_history,
                fontSize = m_text,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = clear_all,
                color = Color.Blue,
                fontSize = m_text,
                modifier = Modifier.clickable(onClick = onClearHistory)
            )
        }

        Spacer(modifier = Modifier.height(small))

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
    val medium = dimensionResource(R.dimen.medium) //16dp
    val large = dimensionResource(R.dimen.large) //24dp

    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(large)
        )
        Spacer(modifier = Modifier.width(medium))
        Text(
            text = query,
            fontSize = m_text
        )
    }
}

