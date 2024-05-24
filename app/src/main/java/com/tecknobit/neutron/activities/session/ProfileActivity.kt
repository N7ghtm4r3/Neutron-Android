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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.NeutronAlertDialog
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.ui.theme.errorLight
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Dark
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Light
import com.tecknobit.neutroncore.records.User.LANGUAGES_SUPPORTED
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import com.tecknobit.neutroncore.records.User.UserStorage.Online
import kotlinx.coroutines.delay

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
                            val showChangeEmailAlert = remember { mutableStateOf(false) }
                            var userEmail by remember { mutableStateOf(user.email) }
                            val newEmail = remember { mutableStateOf("") }
                            UserInfo(
                                header = R.string.email,
                                info = userEmail,
                                onClick = { showChangeEmailAlert.value = true }
                            )
                            NeutronAlertDialog(
                                dismissAction = {
                                    newEmail.value = ""
                                    showChangeEmailAlert.value = false
                                },
                                icon = Icons.Default.Email,
                                show = showChangeEmailAlert,
                                title = R.string.change_email,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = newEmail,
                                        label = R.string.new_email
                                    )
                                },
                                confirmAction = {
                                    // TODO: MAKE THE REQUEST AND SAVE IN LOCAL THEN
                                    userEmail = newEmail.value
                                    user.email = userEmail
                                    showChangeEmailAlert.value = false
                                }
                            )
                            val showChangePasswordAlert = remember { mutableStateOf(false) }
                            val newPassword = remember { mutableStateOf("") }
                            var hiddenPassword by remember { mutableStateOf(true) }
                            UserInfo(
                                header = R.string.password,
                                info = "****",
                                onClick = { showChangePasswordAlert.value = true }
                            )
                            NeutronAlertDialog(
                                dismissAction = {
                                    newPassword.value = ""
                                    showChangePasswordAlert.value = false
                                },
                                icon = Icons.Default.Password,
                                show = showChangePasswordAlert,
                                title = R.string.change_password,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = newPassword,
                                        label = R.string.new_password,
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { hiddenPassword = !hiddenPassword }
                                            ) {
                                                Icon(
                                                    imageVector = if(hiddenPassword)
                                                        Icons.Default.Visibility
                                                    else
                                                        Icons.Default.VisibilityOff,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        visualTransformation = if(hiddenPassword)
                                            PasswordVisualTransformation()
                                        else
                                            VisualTransformation.None,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password
                                        )
                                    )
                                },
                                confirmAction = {
                                    // TODO: MAKE THE REQUEST AND SAVE IN LOCAL THEN
                                    showChangePasswordAlert.value = false
                                }
                            )
                            val changeLanguage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.language,
                                info = LANGUAGES_SUPPORTED[user.language]!!,
                                onClick = { changeLanguage.value = true }
                            )
                            ChangeLanguage(
                                changeLanguage = changeLanguage
                            )
                            val changeTheme = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.theme,
                                info = user.theme.name,
                                buttonText = R.string.change,
                                onClick = { changeTheme.value = true }
                            )
                            ChangeTheme(
                                changeTheme = changeTheme
                            )
                            val showChangeStorage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.storage_data,
                                info = user.storage.name,
                                buttonText = R.string.change,
                                onClick = { showChangeStorage.value = true }
                            )
                            ChangeStorage(
                                changeStorage = showChangeStorage
                            )
                            Column (
                                modifier = Modifier
                                    .padding(
                                        top = 16.dp,
                                        start = 32.dp,
                                        end = 32.dp,
                                        bottom = 16.dp
                                    ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val showLogoutAlert = remember { mutableStateOf(false) }
                                val showDeleteAlert = remember { mutableStateOf(false) }
                                NeutronButton(
                                    onClick = {
                                        showLogoutAlert.value = true
                                    },
                                    text = R.string.logout
                                )
                                NeutronAlertDialog(
                                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                                    show = showLogoutAlert,
                                    title = R.string.logout,
                                    text = R.string.logout_message,
                                    confirmAction = {
                                        // TODO: MAKE THE OPE TO LOGOUT THEN
                                        navToSplash()
                                    }
                                )
                                NeutronButton(
                                    onClick = {
                                        showDeleteAlert.value = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = errorLight
                                    ),
                                    text = R.string.delete
                                )
                                NeutronAlertDialog(
                                    icon = Icons.Default.Cancel,
                                    show = showDeleteAlert,
                                    title = R.string.delete,
                                    text = R.string.delete_message,
                                    confirmAction = {
                                        // TODO: MAKE THE REQUEST THEN
                                        navToSplash()
                                    }
                                )
                            }
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
        buttonText: Int = R.string.edit,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 12.dp
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
                        text = stringResource(buttonText),
                        fontSize = 12.sp
                    )
                }
            }
        }
        HorizontalDivider()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeLanguage(
        changeLanguage: MutableState<Boolean>
    ) {
        ChangeInfo(
            showModal = changeLanguage
        ) {
            LANGUAGES_SUPPORTED.keys.forEach { language ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // TODO: MAKE THE REQUEST THEN
                            user.language = language
                            changeLanguage.value = false
                            navToSplash()
                        }
                        .padding(
                            all = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if(user.language == language)
                            MaterialTheme.colorScheme.primary
                        else
                            LocalContentColor.current
                    )
                    Text(
                        text = LANGUAGES_SUPPORTED[language]!!,
                        fontFamily = displayFontFamily
                    )
                }
                HorizontalDivider()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeTheme(
        changeTheme: MutableState<Boolean>
    ) {
        ChangeInfo(
            showModal = changeTheme
        ) {
            ApplicationTheme.entries.forEach { theme ->
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeStorage(
        changeStorage: MutableState<Boolean>
    ) {
        val currentStorageIsLocal = user.storage == Local
        val hostAddress = remember { mutableStateOf("") }
        val serverSecret = remember { mutableStateOf("") }
        var isExecuting by remember { mutableStateOf(false) }
        val resetLayout = {
            hostAddress.value = ""
            serverSecret.value = ""
            changeStorage.value = false
        }
        ChangeInfo(
            showModal = changeStorage,
            sheetState = rememberModalBottomSheetState(
                confirmValueChange = { !isExecuting }
            )
        ) {
            Column (
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if(!isExecuting) {
                    val awareText = stringResource(
                        if(currentStorageIsLocal)
                            R.string.aware_server_message
                        else
                            R.string.aware_local_message
                    )
                    Text(
                        text = stringResource(R.string.change_storage_location),
                        fontFamily = displayFontFamily,
                        fontSize = 20.sp
                    )
                    Text(
                        text = awareText,
                        textAlign = TextAlign.Justify
                    )
                    if(currentStorageIsLocal) {
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = hostAddress,
                            label = R.string.host_address,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = serverSecret,
                            label = R.string.server_secret
                        )
                    }
                } else {
                    CircularProgressIndicator()
                    // TODO: MAKE THE REQUEST THEN SET THE LOCAL SESSION

                    // TODO: TO REMOVE AND USE
                    LaunchedEffect(key1 = true) {
                        delay(5000L)
                        user.storage = if(currentStorageIsLocal)
                            Online
                        else
                            Local
                        navToSplash()
                    }
                    // TODO: THIS INSTEAD
                    //  user.storage = if(currentStorageIsLocal)
                    //       Online
                    //  else
                    //       Local
                    //  navToSplash()
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            if(isExecuting) {
                                // TODO: STOP THE TRANSFER THEN
                                isExecuting = false
                            } else {
                                resetLayout.invoke()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if(isExecuting)
                                    R.string.cancel
                                else
                                    R.string.dismiss
                            )
                        )
                    }
                    if(!isExecuting) {
                        TextButton(
                            onClick = {
                                // TODO: MAKE THE REQUEST THEN
                                isExecuting = true
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.confirm)
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeInfo(
        showModal: MutableState<Boolean>,
        sheetState: SheetState = rememberModalBottomSheetState(),
        onDismissRequest: () -> Unit = { showModal.value = false },
        content: @Composable ColumnScope.() -> Unit
    ) {
        if(showModal.value) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = onDismissRequest
            ) {
                Column (
                    content = content
                )
            }
        }
    }

    private fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navToSplash() {
        startActivity(Intent(this@ProfileActivity, Splashscreen::class.java))
    }

}