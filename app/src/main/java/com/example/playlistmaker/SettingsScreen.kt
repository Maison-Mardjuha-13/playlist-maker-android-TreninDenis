package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }

    val darkTheme = stringResource(R.string.dark_theme)
    val shareAppText = stringResource(R.string.share_app)
    val contactSupportText = stringResource(R.string.contact_support)
    val userAgreementText = stringResource(R.string.user_agreement)
    val emailTo = stringResource(R.string.email_to)
    val emailSubject = stringResource(R.string.email_subject)
    val emailBody = stringResource(R.string.email_body)
    val offerUrl = stringResource(R.string.offer_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Назад",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onBackClick()
                    },
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Настройки",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = darkTheme,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {newValue -> isDarkTheme = newValue}
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp)
                    .clickable{
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Посмотри это крутое приложение - Playlist Maker!")
                        }
                        ContextCompat.startActivity(context, Intent.createChooser(shareIntent, "Поделиться приложением"), null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shareAppText,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Поделиться"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp)
                    .clickable{
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        ContextCompat.startActivity(context, Intent.createChooser(emailIntent, "Выберите почтовый клиент"), null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contactSupportText,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Поддержка"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp)
                    .clickable {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(offerUrl))
                        ContextCompat.startActivity(context, browserIntent, null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userAgreementText,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Стрелка"
                )
            }
        }
    }
}