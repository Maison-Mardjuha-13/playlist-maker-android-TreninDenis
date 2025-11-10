package com.example.playlistmaker

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PlaylistHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.MAIN.name
    ) {
        composable(AppScreen.MAIN.name) {
            MainScreen(
                onSearchClick = { navigateToSearch(navController) },
                onSettingsClick = { navigateToSettings(navController) }
            )
        }

        composable(AppScreen.SEARCH.name) {
            SearchScreen(
                onBackClick = { navigateToMain(navController) }
            )
        }

        composable(AppScreen.SETTINGS.name) {
            SettingsScreen(
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