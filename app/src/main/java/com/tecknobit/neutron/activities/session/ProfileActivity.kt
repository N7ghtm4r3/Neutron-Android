package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Dark
import com.tecknobit.neutroncore.records.User.ApplicationTheme.Light
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

/**
 * The **ProfileActivity** class is the activity where the user manage his/her profile account
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronActivity
 * @see ComponentActivity
 */
class ProfileActivity : NeutronActivity() {

    /**
     * *theme* -> the current user's theme
     */
    private lateinit var theme: MutableState<ApplicationTheme>

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = ProfileActivityViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * On create method
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * If your ComponentActivity is annotated with {@link ContentView}, this will
     * call {@link #setContentView(int)} for you.
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                                        .error(R.drawable.ic_launcher_playstore)
                                        .build(),
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
                                        mustBeInLowerCase = true,
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

    /**
     * Function to display a specific info details of the user
     *
     * @param header: the header of the info to display
     * @param info: the info details value to display
     * @param buttonText: the text of the setting button
     * @param onClick: the action to execute when the [buttonText] has been clicked
     */
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

    /**
     * Function to allow the user to change the current language setting
     *
     * @param changeLanguage: the state whether display this section
     */
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

    /**
     * Function to allow the user to change the current currency setting
     *
     * @param changeCurrency: the state whether display this section
     * @param currencyValue: the current value of the currency used by the user
     */
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

    /**
     * Function to allow the user to change the current theme setting
     *
     * @param changeTheme: the state whether display this section
     */
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

    /**
     * Function to allow the user to change a current setting
     *
     * @param showModal: the state whether display the [ModalBottomSheet]
     * @param sheetState: the state to apply to the [ModalBottomSheet]
     * @param onDismissRequest: the action to execute when the the [ModalBottomSheet] has been dismissed
     * @param content: the content to display
     */
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

    /**
     * Function to execute the back navigation from the current activity to the previous activity
     *
     * No-any params required
     */
    private fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Function to execute the back navigation from the [Splashscreen] activity after user changed any
     * setting which required the refresh of the [localUser]
     *
     * No-any params required
     */
    private fun navToSplash() {
        startActivity(Intent(this@ProfileActivity, Splashscreen::class.java))
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or {@link #onPause}. This
     * is usually a hint for your activity to start interacting with the user, which is a good
     * indicator that the activity became active and ready to receive input. This sometimes could
     * also be a transit state toward another resting state. For instance, an activity may be
     * relaunched to {@link #onPause} due to configuration changes and the activity was visible,
     * but wasn’t the top-most activity of an activity task. {@link #onResume} is guaranteed to be
     * called before {@link #onPause} in this case which honors the activity lifecycle policy and
     * the activity eventually rests in {@link #onPause}.
     *
     * <p>On platform versions prior to {@link android.os.Build.VERSION_CODES#Q} this is also a good
     * place to try to open exclusive-access devices or to get access to singleton resources.
     * Starting  with {@link android.os.Build.VERSION_CODES#Q} there can be multiple resumed
     * activities in the system simultaneously, so {@link #onTopResumedActivityChanged(boolean)}
     * should be used for that purpose instead.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * Will be set the **[FetcherManager.activeContext]** with the current context
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     * @see #onTopResumedActivityChanged(boolean)
     */
    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}