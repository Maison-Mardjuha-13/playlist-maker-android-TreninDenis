package com.example.playlistmaker.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.ui.theme.PlaylistmakerTheme
import com.example.playlistmaker.ui.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    private val searchViewModel by viewModels<SearchViewModel>{
        SearchViewModel.getViewModelFactory()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaylistmakerTheme {
                val navController = rememberNavController()
                PlaylistHost(navController = navController)
            }
        }
    }
}


