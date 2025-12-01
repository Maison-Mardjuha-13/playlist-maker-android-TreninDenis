package com.example.playlistmaker.ui.main

import SearchViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.domain.models.AppScreen
import com.example.playlistmaker.ui.favourite.FavouriteScreen
import com.example.playlistmaker.ui.playlist.NewPlaylistScreen
import com.example.playlistmaker.ui.playlist.PlaylistDetailsScreen
import com.example.playlistmaker.ui.playlist.PlaylistsScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.settings.SettingsScreen
import com.example.playlistmaker.ui.track.TrackDetailsScreen
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel

@Composable
fun PlaylistHost(navController: NavHostController) {
    val playlistViewModel: PlaylistViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = AppScreen.MAIN.name
    ) {
        composable(AppScreen.MAIN.name) {
            MainScreen(
                onSearchClick = { navigateToSearch(navController) },
                onSettingsClick = { navigateToSettings(navController) },
                onPlaylistClick = { navigateToPlaylist(navController) },
                onFavouriteClick = { navigateToFavourite(navController) }
            )
        }

        composable(AppScreen.SEARCH.name) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                viewModel = searchViewModel,
                onBackClick = { navigateToMain(navController) },
                onTrackClick = { trackId -> navigateToTrackDetails(navController, trackId) }
            )
        }

        composable(AppScreen.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.PLAYLIST.name) {
            PlaylistsScreen(
                modifier = Modifier,
                playlistViewModel = playlistViewModel,
                addNewPlaylist = { navigateToNewPlaylist(navController) },
                navigateToPlaylist = { playlistId -> navigateToPlaylistDetails(navController, playlistId) },
                navigateBack = { navController.popBackStack()},
                navController = navController
            )
        }

        composable(AppScreen.FAVOURITE.name) {
            FavouriteScreen(
                onBackClick = { navigateToMain(navController) },
                onTrackClick = { trackId -> navigateToTrackDetails(navController, trackId) },
                playlistViewModel = playlistViewModel
            )
        }

        composable(AppScreen.NEW_PLAYLIST.name) {
            NewPlaylistScreen(
                playlistViewModel = playlistViewModel,
                onSaveClick = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }

        composable("${AppScreen.TRACK_DETAILS.name}/{trackId}") { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")?.toLongOrNull() ?: 0L
            TrackDetailsScreen(
                trackId = trackId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("${AppScreen.PLAYLIST_DETAILS.name}/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")?.toLongOrNull() ?: 0L
            PlaylistDetailsScreen(
                playlistId = playlistId,
                onBackClick = { navController.popBackStack() },
                onTrackClick = { trackId -> navigateToTrackDetails(navController, trackId) },
                playlistViewModel = playlistViewModel
            )
        }
    }
}

private fun navigateToPlaylistDetails(navController: NavController, playlistId: Long) {
    navController.navigate("${AppScreen.PLAYLIST_DETAILS.name}/$playlistId")
}

private fun navigateToMain(navController: NavController) {
    navController.navigate(AppScreen.MAIN.name) {
        popUpTo(AppScreen.MAIN.name) { inclusive = true }
    }
}

private fun navigateToSearch(navController: NavController) {
    navController.navigate(AppScreen.SEARCH.name)
}

private fun navigateToSettings(navController: NavController) {
    navController.navigate(AppScreen.SETTINGS.name)
}

private fun navigateToPlaylist(navController: NavController) {
    navController.navigate(AppScreen.PLAYLIST.name)
}

private fun navigateToFavourite(navController: NavController) {
    navController.navigate(AppScreen.FAVOURITE.name)
}

private fun navigateToNewPlaylist(navController: NavController) {
    navController.navigate(AppScreen.NEW_PLAYLIST.name)
}

private fun navigateToTrackDetails(navController: NavController, trackId: Long) {
    navController.navigate("${AppScreen.TRACK_DETAILS.name}/$trackId")
}