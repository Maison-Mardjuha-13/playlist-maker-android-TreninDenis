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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.playlistmaker.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.R
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    val xs = dimensionResource(R.dimen.xs) //0.5dp
    val small = dimensionResource(R.dimen.small) //8dp
    val medium = dimensionResource(R.dimen.medium) //16dp
    val large = dimensionResource(R.dimen.large) //24dp
    val exlarge = dimensionResource(R.dimen.exlarge) //32dp
    val xl = dimensionResource(R.dimen.xl) //48.dp
    val xxl = dimensionResource(R.dimen.xxl) //64dp
    val dvesti = dimensionResource(R.dimen.dvesti)


    val m_text = dimensionResource(R.dimen.mediumtext).value.sp //16sp
    val large_text = dimensionResource(R.dimen.largetext).value.sp //24sp

    val new_pl = stringResource(R.string.new_pl)
    val cover_image = stringResource(R.string.cover_image)
    val choose_cover = stringResource(R.string.choose_cover)
    val pl_name = stringResource(R.string.pl_name)
    val pl_description = stringResource(R.string.description)
    val save = stringResource(R.string.save)
    val del_cover = stringResource(R.string.del_cover)

    var localImagePath by remember { mutableStateOf<String?>(null) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            scope.launch {
                val savedPath = playlistViewModel.saveCoverImage(context, selectedUri)
                if (savedPath != null) {
                    localImagePath = savedPath
                    playlistViewModel.setCoverImageUri(savedPath)
                }
            }
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
            .padding(medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(exlarge)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Text(
                text = new_pl,
                fontSize = large_text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = medium)
            )
        }

        Spacer(modifier = Modifier.height(exlarge))
        
        Text(
            text = cover_image,
            fontSize = m_text,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = small)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dvesti)
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
                .padding(small),
            contentAlignment = Alignment.Center
        ) {
            if (coverImageUri != null) {
                AsyncImage(
                    model = Uri.parse(coverImageUri),
                    contentDescription = cover_image,
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
                        modifier = Modifier.size(xxl),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(small))
                    Text(
                        text = choose_cover,
                        color = Color.Gray,
                        fontSize = m_text
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(large))

        Text(pl_name)
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = small)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(xs)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(medium))

        Text(pl_description)
        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = small)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(xs)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(exlarge))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
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
                .height(xl),
            enabled = name.isNotEmpty()
        ) {
            Text(save)
        }


        if (coverImageUri != null) {
            Spacer(modifier = Modifier.height(medium))
            Button(
                onClick = {
                    playlistViewModel.setCoverImageUri(null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xl),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text(del_cover)
            }
        }
    }
}