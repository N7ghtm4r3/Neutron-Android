package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.graphics.ColorFilter
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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.NeutronAlertDialog
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.ui.theme.errorLight
import com.tecknobit.neutron.viewmodels.ProfileActivityViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isHostValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.helpers.InputValidator.isServerSecretValid
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Dark
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Light
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Random
import kotlin.math.min

class ProfileActivity : NeutronActivity() {

    private lateinit var theme: MutableState<ApplicationTheme>

    private lateinit var hostLocalSignIn: MutableState<Boolean>

    private val currentStorageIsLocal = localUser.storage == Local

    private val viewModel = ProfileActivityViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * **scanOptions** ->
     */
    private val scanOptions = ScanOptions()
        .setBeepEnabled(false)
        .setOrientationLocked(false)

    /**
     * **barcodeLauncher** -> the launcher used to start the scan and use the [scanOptions]
     */
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            // TODO: SHARE THE DATA TO THE QRCODE SCANNED
        }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            scanOptions.setPrompt(stringResource(R.string.qr_scanner_prompt_message))
            theme = remember { mutableStateOf(localUser.theme) }
            val profilePic = remember { mutableStateOf(localUser.profilePic) }
            val photoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { imageUri ->
                    if(imageUri != null) {
                        val imagePath = getFilePath(
                            context = this@ProfileActivity,
                            uri = imageUri
                        )
                        viewModel.changeProfilePic(
                            imagePath = imagePath!!,
                            profilePic = profilePic
                        )
                    }
                }
            )
            NeutronTheme (
                darkTheme = when(theme.value) {
                    Light -> false
                    Dark -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        val showDeleteAlert = remember { mutableStateOf(false) }
                        FloatingActionButton(
                            onClick = {
                                showDeleteAlert.value = true
                            },
                            containerColor = errorLight
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                        }
                        NeutronAlertDialog(
                            icon = Icons.Default.Cancel,
                            show = showDeleteAlert,
                            title = R.string.delete,
                            text = R.string.delete_message,
                            confirmAction = {
                                viewModel.deleteAccount {
                                    navToSplash()
                                }
                            }
                        )
                    }
                ) {
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
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ImageOnly
                                                )
                                            )
                                        },
                                    contentScale = ContentScale.Crop,
                                    model = ImageRequest.Builder(this@ProfileActivity)
                                        .data(profilePic.value)
                                        .crossfade(true)
                                        .crossfade(500)
                                        .build(),
                                    //TODO: USE THE REAL IMAGE ERROR .error(),
                                    contentDescription = null
                                )
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column {
                                        IconButton(
                                            modifier = Modifier
                                                .padding(
                                                    top = 16.dp
                                                )
                                                .align(Alignment.Start),
                                            onClick = { navBack() }
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                    if(currentStorageIsLocal) {
                                        hostLocalSignIn = remember { mutableStateOf(false) }
                                        HostLocalSignIn()
                                        Column (
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Row {
                                                IconButton(
                                                    modifier = Modifier
                                                        .padding(
                                                            top = 16.dp
                                                        ),
                                                    onClick = { hostLocalSignIn.value = true }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lan,
                                                        contentDescription = null,
                                                        tint = Color.White
                                                    )
                                                }
                                                IconButton(
                                                    modifier = Modifier
                                                        .padding(
                                                            top = 16.dp
                                                        ),
                                                    onClick = { barcodeLauncher.launch(scanOptions) }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.QrCodeScanner,
                                                        contentDescription = null,
                                                        tint = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            start = 16.dp,
                                            bottom = 16.dp
                                        )
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart),
                                    text = localUser.completeName,
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
                            var userEmail by remember { mutableStateOf(localUser.email) }
                            viewModel.newEmail = remember { mutableStateOf("") }
                            viewModel.newEmailError = remember { mutableStateOf(false) }
                            val resetEmailLayout = {
                                viewModel.newEmail.value = ""
                                viewModel.newEmailError.value = false
                                showChangeEmailAlert.value = false
                            }
                            UserInfo(
                                header = R.string.email,
                                info = userEmail,
                                onClick = { showChangeEmailAlert.value = true }
                            )
                            NeutronAlertDialog(
                                onDismissAction = resetEmailLayout,
                                icon = Icons.Default.Email,
                                show = showChangeEmailAlert,
                                title = R.string.change_email,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = viewModel.newEmail,
                                        label = R.string.new_email,
                                        errorText = R.string.email_not_valid,
                                        isError = viewModel.newEmailError,
                                        validator = { isEmailValid(it) }
                                    )
                                },
                                confirmAction = {
                                    viewModel.changeEmail(
                                        onSuccess = {
                                            userEmail = viewModel.newEmail.value
                                            resetEmailLayout.invoke()
                                        }
                                    )
                                }
                            )
                            val showChangePasswordAlert = remember { mutableStateOf(false) }
                            viewModel.newPassword = remember { mutableStateOf("") }
                            viewModel.newPasswordError = remember { mutableStateOf(false) }
                            val resetPasswordLayout = {
                                viewModel.newPassword.value = ""
                                viewModel.newPasswordError.value = false
                                showChangePasswordAlert.value = false
                            }
                            var hiddenPassword by remember { mutableStateOf(true) }
                            UserInfo(
                                header = R.string.password,
                                info = "****",
                                onClick = { showChangePasswordAlert.value = true }
                            )
                            NeutronAlertDialog(
                                onDismissAction = resetPasswordLayout,
                                icon = Icons.Default.Password,
                                show = showChangePasswordAlert,
                                title = R.string.change_password,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = viewModel.newPassword,
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
                                        ),
                                        errorText = R.string.password_not_valid,
                                        isError = viewModel.newPasswordError,
                                        validator = { isPasswordValid(it) }
                                    )
                                },
                                confirmAction = {
                                    viewModel.changePassword(
                                        onSuccess = resetPasswordLayout
                                    )
                                }
                            )
                            val changeLanguage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.language,
                                info = LANGUAGES_SUPPORTED[localUser.language]!!,
                                onClick = { changeLanguage.value = true }
                            )
                            ChangeLanguage(
                                changeLanguage = changeLanguage
                            )
                            val changeCurrency = remember { mutableStateOf(false) }
                            val currency = remember { mutableStateOf(localUser.currency.isoName) }
                            UserInfo(
                                header = R.string.currency,
                                info = currency.value,
                                onClick = { changeCurrency.value = true }
                            )
                            ChangeCurrency(
                                changeCurrency = changeCurrency,
                                currencyValue = currency
                            )
                            val changeTheme = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.theme,
                                info = localUser.theme.name,
                                buttonText = R.string.change,
                                onClick = { changeTheme.value = true }
                            )
                            ChangeTheme(
                                changeTheme = changeTheme
                            )
                            val showChangeStorage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.storage_data,
                                info = localUser.storage.name,
                                buttonText = R.string.change,
                                onClick = { showChangeStorage.value = true }
                            )
                            ChangeStorage(
                                changeStorage = showChangeStorage
                            )
                            val showLogoutAlert = remember { mutableStateOf(false) }
                            UserInfo(
                                header = R.string.disconnect,
                                info = stringResource(id = R.string.logout),
                                buttonText = R.string.execute,
                                onClick = { showLogoutAlert.value = true }
                            )
                            NeutronAlertDialog(
                                icon = Icons.AutoMirrored.Filled.ExitToApp,
                                show = showLogoutAlert,
                                title = R.string.logout,
                                text = R.string.logout_message,
                                confirmAction = {
                                    viewModel.clearSession {
                                        navToSplash()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HostLocalSignIn() {
        val isListening = remember { mutableStateOf(true) }
        // TODO: TO REMOVE GET FROM THE REAL REQUEST RESPONSE
        val success = remember { mutableStateOf(Random().nextBoolean()) }
        if(hostLocalSignIn.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(
                    confirmValueChange = { !isListening.value }
                ),
                onDismissRequest = {
                    if(!isListening.value)
                        hostLocalSignIn.value = false
                }
            ) {
                // TODO: IMPLEMENT THE SOCKETMANAGER OR THE WRAPPER CLASS TO EXECUTE THE HOSTING AND THE DATA TRANSFER

                // TODO: TO REMOVE MAKE THE REAL WORKFLOW INSTEAD
                LaunchedEffect(
                    key1 = true
                ) {
                    delay(3000L)
                    isListening.value = false
                }
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if(isListening.value) {
                        Text(
                            text = stringResource(R.string.hosting_local_sign_in),
                            fontFamily = displayFontFamily,
                            fontSize = 20.sp
                        )
                    }
                    ResponseStatusUI(
                        isWaiting = isListening,
                        statusText = R.string.waiting_for_the_request,
                        isSuccessful = success,
                        successText = R.string.sign_in_executed_successfully,
                        failedText = R.string.sign_in_failed_message
                    )
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {
                            // TODO: CLOSE THE LISTENING THEN
                            hostLocalSignIn.value = false
                            isListening.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if(isListening.value)
                                    R.string.cancel
                                else
                                    R.string.close
                            )
                        )
                    }
                }
            }
        } else
            isListening.value = true
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
                            viewModel.changeLanguage(
                                newLanguage = language,
                                onSuccess = {
                                    changeLanguage.value = false
                                    navToSplash()
                                }
                            )
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
                        tint = if (localUser.language == language)
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
    private fun ChangeCurrency(
        changeCurrency: MutableState<Boolean>,
        currencyValue: MutableState<String>
    ) {
        ChangeInfo(
            showModal = changeCurrency
        ) {
            NeutronCurrency.entries.forEach { currency ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.changeCurrency(
                                newCurrency = currency,
                                onSuccess = {
                                    currencyValue.value = currency.isoName
                                    changeCurrency.value = false
                                }
                            )
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
                        tint = if (localUser.currency == currency)
                            MaterialTheme.colorScheme.primary
                        else
                            LocalContentColor.current
                    )
                    Text(
                        text = "${currency.isoName} ${currency.isoCode}",
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
                            viewModel.changeTheme(
                                newTheme = theme,
                                onChange = {
                                    changeTheme.value = false
                                    this@ProfileActivity.theme.value = theme
                                }
                            )
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
                        tint = if (localUser.theme == theme)
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
        viewModel.hostAddress = remember { mutableStateOf("") }
        viewModel.hostError = remember { mutableStateOf(false) }
        viewModel.serverSecret = remember { mutableStateOf("") }
        viewModel.serverSecretError = remember { mutableStateOf(false) }
        viewModel.isExecuting = remember { mutableStateOf(false) }
        viewModel.waiting = remember { mutableStateOf(true) }
        viewModel.success = remember { mutableStateOf(false) }
        val executeRequest = {
            viewModel.isExecuting.value = true
            viewModel.waiting.value = true
            viewModel.success.value = false
            viewModel.changeStorage()
        }
        val resetLayout = {
            viewModel.isExecuting.value = false
            viewModel.hostAddress.value = ""
            viewModel.hostError.value = false
            viewModel.serverSecret.value = ""
            viewModel.serverSecretError.value = false
            changeStorage.value = false
            viewModel.waiting.value = true
            viewModel.success.value = false
        }
        ChangeInfo(
            showModal = changeStorage,
            sheetState = rememberModalBottomSheetState(
                confirmValueChange = { !viewModel.isExecuting.value || viewModel.success.value }
            ),
            onDismissRequest = { resetLayout.invoke() }
        ) {
            Column (
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if(!viewModel.isExecuting.value) {
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
                            value = viewModel.hostAddress,
                            label = R.string.host_address,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            errorText = R.string.host_address_not_valid,
                            isError = viewModel.hostError,
                            validator = { isHostValid(it) }
                        )
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = viewModel.serverSecret,
                            label = R.string.server_secret,
                            errorText = R.string.server_secret_not_valid,
                            isError = viewModel.serverSecretError,
                            validator = { isServerSecretValid(it) }
                        )
                    }
                } else {
                    ResponseStatusUI(
                        isWaiting = viewModel.waiting,
                        statusText = R.string.transferring_data,
                        isSuccessful = viewModel.success,
                        successText = R.string.transfer_executed_successfully,
                        failedText = R.string.transfer_failed
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    if(!viewModel.isExecuting.value) {
                        TextButton(
                            onClick = { resetLayout.invoke() }
                        ) {
                            Text(
                                text = stringResource(
                                    if(viewModel.isExecuting.value)
                                        R.string.cancel
                                    else
                                        R.string.dismiss
                                )
                            )
                        }
                        TextButton(
                            onClick = { executeRequest.invoke() }
                        ) {
                            Text(
                                text = stringResource(R.string.confirm)
                            )
                        }
                    } else {
                        TextButton(
                            onClick = {
                                if(viewModel.waiting.value)
                                    viewModel.isExecuting.value = false
                                else {
                                    if(viewModel.success.value) {
                                        resetLayout.invoke()
                                        changeStorage.value = false
                                        navToSplash()
                                    } else
                                        executeRequest.invoke()
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if(viewModel.waiting.value)
                                        R.string.cancel
                                    else {
                                        if(viewModel.success.value)
                                            R.string.close
                                        else
                                            R.string.retry
                                    }
                                )
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

    /**
     * Function to get the complete file path of an file
     *
     * @param context: the context where the file is needed
     * @param uri: the uri of the file
     * @return the path of the file
     */
    private fun getFilePath(
        context: Context,
        uri: Uri
    ): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream?.close()
            outputStream.close()
        } catch (_: Exception) {
        } finally {
            returnCursor.close()
        }
        return file.path
    }

    @Composable
    private fun ResponseStatusUI(
        isWaiting: MutableState<Boolean>,
        statusText: Int,
        isSuccessful: MutableState<Boolean>,
        successText: Int,
        failedText: Int
    ) {
        if(isWaiting.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(
                        top = 20.dp
                    )
                    .size(75.dp)
            )
            Text(
                modifier = Modifier
                    .padding(
                        top = 10.dp
                    ),
                text = stringResource(statusText),
                fontSize = 14.sp
            )
        } else {
            Image(
                modifier = Modifier
                    .size(125.dp),
                imageVector = if(isSuccessful.value)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Cancel,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if(isSuccessful.value)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            )
            Text(
                text = stringResource(
                    if (isSuccessful.value)
                        successText
                    else
                        failedText
                ),
                fontSize = 14.sp
            )
        }
    }

    private fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navToSplash() {
        startActivity(Intent(this@ProfileActivity, Splashscreen::class.java))
    }

    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}