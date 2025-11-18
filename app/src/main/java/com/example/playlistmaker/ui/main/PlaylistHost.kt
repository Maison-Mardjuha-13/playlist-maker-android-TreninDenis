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
import com.example.playlistmaker.ui.playlist.PlaylistScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.settings.SettingsScreen
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
                modifier = Modifier
            )
        }

        composable(AppScreen.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.PLAYLIST.name) {
            PlaylistScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.FAVOURITE.name) {
            FavouriteScreen(
                onBackClick = { navigateToMain(navController) }
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