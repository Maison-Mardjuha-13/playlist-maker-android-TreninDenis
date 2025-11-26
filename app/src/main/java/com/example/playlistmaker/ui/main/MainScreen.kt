package com.example.playlistmaker.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

//@Composable
//fun MainScreen(
//    onSearchClick: () -> Unit,
//    onSettingsClick: () -> Unit,
//    onPlaylistClick: () -> Unit,
//    onFavouriteClick: () -> Unit
//) {
//
//    val xxl = dimensionResource(R.dimen.xxl) //64dp
//    val xxxl = dimensionResource(R.dimen.xxxl) //100dp
//    val small = dimensionResource(R.dimen.small) //8dp
//    val medium = dimensionResource(R.dimen.medium) //16dp
//    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp
//    val main_title = stringResource(R.string.main_name)
//    val search_title = stringResource(R.string.search_name)
//    val playlist_title = stringResource(R.string.playlist_name)
//    val fav_title = stringResource(R.string.favourite_name)
//    val settings_title = stringResource(R.string.settings_name)
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(xxxl)
//                .statusBarsPadding()
//                .background(Color.Blue),
//        ) {
//            Text(
//                text = main_title,
//                color = Color.White,
//                fontSize = large_text,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(small)
//
//        ) {
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(xxl)
//                    .padding(small)
//                    .clickable {
//                        onSearchClick()
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = search_title
//                )
//                Spacer(modifier = Modifier.width(medium))
//                Text(
//                    text = search_title,
//                    fontSize = large_text,
//                    modifier = Modifier.weight(1f)
//                )
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = null
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(xxl)
//                    .padding(small)
//                    .clickable { onPlaylistClick() },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.List,
//                    contentDescription = playlist_title
//                )
//                Spacer(modifier = Modifier.width(medium))
//                Text(
//                    text = playlist_title,
//                    fontSize = large_text,
//                    modifier = Modifier.weight(1f)
//                )
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = null
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(xxl)
//                    .padding(small)
//                    .clickable {onFavouriteClick()},
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Favorite,
//                    contentDescription = fav_title
//                )
//                Spacer(modifier = Modifier.width(medium))
//                Text(
//                    text = fav_title,
//                    fontSize = large_text,
//                    modifier = Modifier.weight(1f)
//                )
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = null
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(xxl)
//                    .padding(small)
//                    .clickable {
//                        onSettingsClick()
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = settings_title
//                )
//                Spacer(modifier = Modifier.width(medium))
//                Text(
//                    text = settings_title,
//                    fontSize = large_text,
//                    modifier = Modifier.weight(1f)
//                )
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = null
//                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Нажмите кнопку для открытия BottomSheet")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Это панель ModalBottomSheet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Здесь может быть ваш контент")
                }
            }
        }
    }
}