package com.tecknobit.neutron.activities.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.tecknobit.neutron.R
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.viewmodels.ConnectActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Random

class ConnectActivity : ComponentActivity() {

    private lateinit var isSignUp: MutableState<Boolean>

    private lateinit var storeDataOnline: MutableState<Boolean>

    private lateinit var showQrCodeLogin: MutableState<Boolean>

    private var localDatabaseNotExists: Boolean = true

    private val snackbarHostState by lazy {
        SnackbarHostState()
    }

    private val connectActivityViewModel = ConnectActivityViewModel(
        context = this,
        snackbarHostState = snackbarHostState
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            isSignUp = remember { mutableStateOf(true) }
            storeDataOnline = remember { mutableStateOf(false) }
            showQrCodeLogin = remember { mutableStateOf(false) }
            localDatabaseNotExists = Random().nextBoolean() // TODO: TO INIT CORRECTLY FETCHING THE DATABASE
            NeutronTheme {
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = !isSignUp.value && !storeDataOnline.value && localDatabaseNotExists
                        ) {
                            FloatingActionButton(
                                onClick = { showQrCodeLogin.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null
                                )
                            }
                            LoginQrCode()
                        }
                    }
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        HeaderSection()
                        FormSection()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    @Composable
    private fun HeaderSection() {
        Column (
            modifier = Modifier
                .height(110.dp)
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                R.string.hello
                            else
                                R.string.welcome_back
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                R.string.sign_up
                            else
                                R.string.sign_in
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White,
                        fontSize = 35.sp
                    )
                }
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                val intent = Intent()
                                intent.data = Uri.parse("https://github.com/N7ghtm4r3/Neutron-Android")
                                intent.action = Intent.ACTION_VIEW
                                startActivity(intent)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.github),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "v. ${stringResource(R.string.app_version)}",
                            fontFamily = displayFontFamily,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun FormSection() {
        val host = remember { mutableStateOf("") }
        val serverSecret = remember { mutableStateOf("") }
        val name = remember { mutableStateOf("") }
        val surname = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Switch(
                        checked = storeDataOnline.value,
                        onCheckedChange = { storeDataOnline.value = it }
                    )
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                R.string.store_data_online
                            else
                                R.string.stored_data_online
                        )
                    )
                }
                val keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
                AnimatedVisibility(
                    visible = storeDataOnline.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = host,
                            label = R.string.host_address,
                            keyboardOptions = keyboardOptions
                        )
                        AnimatedVisibility(
                            visible = isSignUp.value
                        ) {
                            NeutronOutlinedTextField(
                                value = serverSecret,
                                label = R.string.server_secret,
                                keyboardOptions = keyboardOptions
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isSignUp.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = name,
                            label = R.string.name,
                            keyboardOptions = keyboardOptions

                        )
                        NeutronOutlinedTextField(
                            value = surname,
                            label = R.string.surname,
                            keyboardOptions = keyboardOptions
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !isSignUp.value && !storeDataOnline.value && localDatabaseNotExists
                ) {
                    Text(
                        modifier = Modifier
                            .width(300.dp),
                        text = stringResource(R.string.local_sign_in_message),
                        textAlign = TextAlign.Justify,
                        fontSize = 12.sp
                    )
                }
                NeutronOutlinedTextField(
                    value = email,
                    label = R.string.email,
                    keyboardOptions = keyboardOptions
                )
                var hiddenPassword by remember { mutableStateOf(true) }
                NeutronOutlinedTextField(
                    value = password,
                    label = R.string.password,
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
                NeutronButton(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .width(300.dp),
                    onClick = {
                        if (isSignUp.value) {
                            connectActivityViewModel.signUp(
                                hostAddress = host.value,
                                serverSecret = serverSecret.value,
                                name = name.value,
                                surname = surname.value,
                                email = email.value,
                                password = password.value
                            )
                        } else {
                            connectActivityViewModel.signIn(
                                hostAddress = host.value,
                                email = email.value,
                                password = password.value,
                            )
                        }
                    },
                    text = if(isSignUp.value)
                        R.string.sign_up_btn
                    else
                        R.string.sign_in_btn
                )
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                R.string.have_an_account
                            else
                                R.string.are_you_new_to_neutron
                        ),
                        fontSize = 14.sp
                    )
                    Text(
                        modifier = Modifier
                            .clickable { isSignUp.value = !isSignUp.value },
                        text = stringResource(
                            if(isSignUp.value)
                                R.string.sign_in_btn
                            else
                                R.string.sign_up_btn
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun LoginQrCode() {
        if(showQrCodeLogin.value) {
            ModalBottomSheet(
                onDismissRequest = { showQrCodeLogin.value = false }
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = 16.dp
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .size(175.dp),
                        painter = rememberQrBitmapPainter(
                            // TODO: TO CREATE THE SESSION WITH THE SOCKETMANAGER TO PASS IN THE QRCODE DATA
                            JSONObject()
                                .put("data", "real_data")
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 10.dp
                            ),
                        text = stringResource(R.string.signup_qr_code_title)
                    )
                }
            }
        }
    }

    /**
     * Function to create the [BitmapPainter] to display the qrcode created
     *
     * @param content: the content to display
     * @param size: the size of the container which contains the qrcode created, default value is 100.[dp]
     * @param padding: the padding of the container, default value is 0.[dp]
     */
    @Composable
    private fun rememberQrBitmapPainter(
        content: JSONObject,
        size: Dp = 100.dp,
        padding: Dp = 0.dp
    ): BitmapPainter {
        val contentSource = content.toString(4)
        var showProgress by remember { mutableStateOf(true) }
        if (showProgress) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(125.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        val density = LocalDensity.current
        val sizePx = with(density) { size.roundToPx() }
        val paddingPx = with(density) { padding.roundToPx() }
        var bitmap by remember(contentSource) { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(bitmap) {
            if (bitmap != null) return@LaunchedEffect
            launch(Dispatchers.IO) {
                val qrCodeWriter = QRCodeWriter()
                val encodeHints = mutableMapOf<EncodeHintType, Any?>()
                    .apply {
                        this[EncodeHintType.MARGIN] = paddingPx
                    }
                val bitmapMatrix = try {
                    qrCodeWriter.encode(
                        contentSource, BarcodeFormat.QR_CODE,
                        sizePx, sizePx, encodeHints
                    )
                } catch (ex: WriterException) {
                    null
                }
                val matrixWidth = bitmapMatrix?.width ?: sizePx
                val matrixHeight = bitmapMatrix?.height ?: sizePx
                val newBitmap = Bitmap.createBitmap(
                    bitmapMatrix?.width ?: sizePx,
                    bitmapMatrix?.height ?: sizePx,
                    Bitmap.Config.ARGB_8888,
                )
                for (x in 0 until matrixWidth) {
                    for (y in 0 until matrixHeight) {
                        val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
                        val pixelColor = if (shouldColorPixel)
                            Color.Black.toArgb()
                        else
                            Color.White.toArgb()
                        newBitmap.setPixel(x, y, pixelColor)
                    }
                }
                bitmap = newBitmap
            }
        }
        return remember(bitmap) {
            val currentBitmap = bitmap ?: Bitmap.createBitmap(
                sizePx, sizePx,
                Bitmap.Config.ARGB_8888,
            ).apply { eraseColor(android.graphics.Color.TRANSPARENT) }
            showProgress = false
            BitmapPainter(currentBitmap.asImageBitmap())
        }
    }

}