package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Dark
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Light
import com.tecknobit.neutroncore.records.User.ApplicationTheme.entries

class ProfileActivity : NeutronActivity() {

    lateinit var theme: MutableState<ApplicationTheme>

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            theme = remember { mutableStateOf(user.theme) }
            NeutronTheme (
                darkTheme = when(theme.value) {
                    Light -> false
                    Dark -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                Scaffold {
                    DisplayContent(
                        modifier = Modifier
                            .padding(
                                top = it.calculateTopPadding()
                            ),
                        contentPadding = 0.dp,
                        cardHeight = 225.dp,
                        cardContent = {
                            Box (
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            // TODO: CHANGE PROFILE PICTURE
                                        },
                                    contentScale = ContentScale.Crop,
                                    model = ImageRequest.Builder(this@ProfileActivity)
                                        .data(user.profilePic)
                                        .crossfade(true)
                                        .crossfade(500)
                                        .build(),
                                    //TODO: USE THE REAL IMAGE ERROR .error(),
                                    contentDescription = null
                                )
                                IconButton(
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp
                                        )
                                        .align(Alignment.TopStart),
                                    onClick = { navBack() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            start = 16.dp,
                                            bottom = 16.dp
                                        )
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart),
                                    text = user.completeName,
                                    color = Color.White,
                                    fontFamily = displayFontFamily,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    ) {
                        Column (
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            UserInfo(
                                header = R.string.email,
                                info = user.email,
                                onClick = {

                                }
                            )
                            UserInfo(
                                header = R.string.password,
                                info = "****",
                                onClick = {

                                }
                            )
                            UserInfo(
                                header = R.string.language,
                                info = user.language,
                                onClick = {

                                }
                            )
                            val changeTheme = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.theme,
                                info = user.theme.name,
                                onClick = { changeTheme.value = true }
                            )
                            ChangeTheme(
                                changeTheme = changeTheme
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun UserInfo(
        header: Int,
        info: String,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 16.dp
                )
        ) {
            Text(
                text = stringResource(header),
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = info,
                    fontSize = 20.sp,
                    fontFamily = displayFontFamily
                )
                Button(
                    modifier = Modifier
                        .height(25.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 10.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.edit),
                        fontSize = 12.sp
                    )
                }
            }
        }
        HorizontalDivider()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeTheme(
        changeTheme: MutableState<Boolean>
    ) {
        if(changeTheme.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(),
                onDismissRequest = { changeTheme.value = false }
            ) {
                Column {
                    entries.forEach { theme ->
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // TODO: MAKE THE REQUEST THEN
                                    user.theme = theme
                                    changeTheme.value = false
                                    this@ProfileActivity.theme.value = theme
                                }
                                .padding(
                                    all = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = when(theme) {
                                    Light -> Icons.Default.LightMode
                                    Dark -> Icons.Default.DarkMode
                                    else -> Icons.Default.AutoMode
                                },
                                contentDescription = null,
                                tint = if(user.theme == theme)
                                    MaterialTheme.colorScheme.primary
                                else
                                    LocalContentColor.current
                            )
                            Text(
                                text = theme.toString(),
                                fontFamily = displayFontFamily
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    private fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}