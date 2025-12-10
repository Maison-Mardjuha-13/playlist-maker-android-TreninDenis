package com.example.playlistmaker.ui.settings

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }

    val small = dimensionResource(R.dimen.small)
    val medium_size = dimensionResource(R.dimen.medium)
    val large_size = dimensionResource(R.dimen.large)
    val exlarge_size = dimensionResource(R.dimen.exlarge)
    val largetext_size = dimensionResource(R.dimen.largetext).value.sp
    var set_title = stringResource(R.string.settings_name)
    val xxl = dimensionResource(R.dimen.xxl) //64dp
    val darkTheme = stringResource(R.string.dark_theme)
    val shareAppText = stringResource(R.string.share_app)
    val contactSupportText = stringResource(R.string.contact_support)
    val userAgreementText = stringResource(R.string.user_agreement)
    val emailTo = stringResource(R.string.email_to)
    val emailSubject = stringResource(R.string.email_subject)
    val emailBody = stringResource(R.string.email_body)
    val offerUrl = stringResource(R.string.offer_url)
    val share_text = stringResource(R.string.share_text)
    val share = stringResource(R.string.share)
    val choseem = stringResource(R.string.choose_email)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(medium_size)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = large_size),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(exlarge_size)
                    .clickable {
                        onBackClick()
                    },
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(medium_size))
            Text(
                text = set_title,
                fontSize = largetext_size,
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
                    .height(xxl)
                    .padding(small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = darkTheme,
                    fontSize = largetext_size,
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
                    .height(xxl)
                    .padding(small)
                    .clickable{
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, share_text)
                        }
                        val chooserIntent = Intent.createChooser(shareIntent, share)
                        ContextCompat.startActivities(context, arrayOf(chooserIntent), null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shareAppText,
                    fontSize = largetext_size,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xxl)
                    .padding(small)
                    .clickable{
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        val chooserIntent = Intent.createChooser(emailIntent, choseem)
                        ContextCompat.startActivities(context, arrayOf(chooserIntent), null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contactSupportText,
                    fontSize = largetext_size,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(xxl)
                    .padding(small)
                    .clickable {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(offerUrl))
                        ContextCompat.startActivities(context, arrayOf(browserIntent), null)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userAgreementText,
                    fontSize = largetext_size,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}