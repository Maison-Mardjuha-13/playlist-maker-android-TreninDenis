package com.example.playlistmaker.ui.playlist

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.R

@Composable
fun NewPlaylistScreen(
    playlistViewModel: PlaylistViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    navController: NavHostController
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coverImageUri by playlistViewModel.coverImageUri.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            playlistViewModel.setCoverImageUri(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Text(
                text = "Новый плейлист",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Секция для выбора обложки
        Text(
            text = "Обложка плейлиста",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                                imagePickerLauncher.launch("image/*")
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }
                }
                .background(Color.LightGray.copy(alpha = 0.2f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (coverImageUri != null) {
                AsyncImage(
                    model = Uri.parse(coverImageUri),
                    contentDescription = "Обложка плейлиста",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cover_photo_add),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите чтобы выбрать обложку",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Название плейлиста")
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Описание")
        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    println("NewPlaylistScreen: Creating playlist '$name' with description '$description' and cover: $coverImageUri")
                    playlistViewModel.createNewPlayList(name, description)
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "playlist_updated",
                        true
                    )
                    onSaveClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = name.isNotEmpty()
        ) {
            Text("Сохранить")
        }


        if (coverImageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    playlistViewModel.setCoverImageUri(null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text("Удалить обложку")
            }
        }
    }
}