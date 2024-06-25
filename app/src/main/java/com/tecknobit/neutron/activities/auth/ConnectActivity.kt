package com.tecknobit.neutron.activities.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.viewmodels.ConnectActivityViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isHostValid
import com.tecknobit.neutroncore.helpers.InputValidator.isNameValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.helpers.InputValidator.isServerSecretValid
import com.tecknobit.neutroncore.helpers.InputValidator.isSurnameValid

class ConnectActivity : ComponentActivity() {

    private val snackbarHostState by lazy {
        SnackbarHostState()
    }

    private val viewModel = ConnectActivityViewModel(
        context = this,
        snackbarHostState = snackbarHostState
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.isSignUp = remember { mutableStateOf(true) }
            viewModel.host = remember { mutableStateOf("") }
            viewModel.hostError = remember { mutableStateOf(false) }
            viewModel.serverSecret = remember { mutableStateOf("") }
            viewModel.serverSecretError = remember { mutableStateOf(false) }
            viewModel.name = remember { mutableStateOf("") }
            viewModel.nameError = remember { mutableStateOf(false) }
            viewModel.surname = remember { mutableStateOf("") }
            viewModel.surnameError = remember { mutableStateOf(false) }
            viewModel.email = remember { mutableStateOf("") }
            viewModel.emailError = remember { mutableStateOf(false) }
            viewModel.password = remember { mutableStateOf("") }
            viewModel.passwordError = remember { mutableStateOf(false) }
            NeutronTheme {
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                            if (viewModel.isSignUp.value)
                                R.string.hello
                            else
                                R.string.welcome_back
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(
                            if (viewModel.isSignUp.value)
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
                val keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
                NeutronOutlinedTextField(
                    value = viewModel.host,
                    label = R.string.host_address,
                    keyboardOptions = keyboardOptions,
                    errorText = R.string.host_address_not_valid,
                    isError = viewModel.hostError,
                    validator = { isHostValid(it) }
                )
                AnimatedVisibility(
                    visible = viewModel.isSignUp.value
                ) {
                    NeutronOutlinedTextField(
                        value = viewModel.serverSecret,
                        label = R.string.server_secret,
                        keyboardOptions = keyboardOptions,
                        errorText = R.string.server_secret_not_valid,
                        isError = viewModel.serverSecretError,
                        validator = { isServerSecretValid(it) }
                    )
                }
                AnimatedVisibility(
                    visible = viewModel.isSignUp.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = viewModel.name,
                            label = R.string.name,
                            keyboardOptions = keyboardOptions,
                            errorText = R.string.name_not_valid,
                            isError = viewModel.nameError,
                            validator = { isNameValid(it) }
                        )
                        NeutronOutlinedTextField(
                            value = viewModel.surname,
                            label = R.string.surname,
                            keyboardOptions = keyboardOptions,
                            errorText = R.string.surname_not_valid,
                            isError = viewModel.surnameError,
                            validator = { isSurnameValid(it) }
                        )
                    }
                }
                NeutronOutlinedTextField(
                    value = viewModel.email,
                    label = R.string.email,
                    mustBeInLowerCase = true,
                    keyboardOptions = keyboardOptions,
                    errorText = R.string.email_not_valid,
                    isError = viewModel.emailError,
                    validator = { isEmailValid(it) }
                )
                var hiddenPassword by remember { mutableStateOf(true) }
                NeutronOutlinedTextField(
                    value = viewModel.password,
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
                    ),
                    errorText = R.string.password_not_valid,
                    isError = viewModel.passwordError,
                    validator = { isPasswordValid(it) }
                )
                NeutronButton(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .width(300.dp),
                    onClick = { viewModel.auth() },
                    text = if (viewModel.isSignUp.value)
                        R.string.sign_up_btn
                    else
                        R.string.sign_in_btn
                )
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (viewModel.isSignUp.value)
                                R.string.have_an_account
                            else
                                R.string.are_you_new_to_neutron
                        ),
                        fontSize = 14.sp
                    )
                    Text(
                        modifier = Modifier
                            .clickable { viewModel.isSignUp.value = !viewModel.isSignUp.value },
                        text = stringResource(
                            if (viewModel.isSignUp.value)
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

}