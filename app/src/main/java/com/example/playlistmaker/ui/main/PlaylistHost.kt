package com.example.playlistmaker.ui.main

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
import com.example.playlistmaker.ui.playlist.PlaylistsScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.settings.SettingsScreen
import com.example.playlistmaker.ui.track.TrackDetailsScreen
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.ui.viewmodel.SearchViewModel

@Composable
fun PlaylistHost(navController: NavHostController) {
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
                modifier = Modifier,
                onTrackClick = { trackId -> navigateToTrackDetails(navController, trackId) }
            )
        }

        composable(AppScreen.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.PLAYLIST.name) {
            val playlistViewModel: PlaylistViewModel = viewModel()
            PlaylistsScreen(
                modifier = Modifier,
                playlistViewModel = playlistViewModel,
                addNewPlaylist = { navigateToNewPlaylist(navController) },
                navigateToPlaylist = { playlistId -> },
                navigateBack = { navController.popBackStack()},
                navController = navController
            )
        }

        composable(AppScreen.FAVOURITE.name) {
            FavouriteScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.NEW_PLAYLIST.name) {
            val playlistViewModel: PlaylistViewModel = viewModel()
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
    }
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