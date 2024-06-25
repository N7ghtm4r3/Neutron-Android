package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import java.io.File

class ProfileActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var newEmail: MutableState<String>

    lateinit var newEmailError: MutableState<Boolean>

    lateinit var newPassword: MutableState<String>

    lateinit var newPasswordError: MutableState<Boolean>

    fun changeProfilePic(
        imagePath: String,
        profilePic: MutableState<String>
    ) {
        requester.sendRequest(
            request = {
                requester.changeProfilePic(
                    profilePic = File(imagePath)
                )
            },
            onSuccess = {
                profilePic.value = imagePath
                localUser.profilePic = it.getString(PROFILE_PIC_KEY)
            },
            onFailure = { showSnack(it) }
        )
    }

    fun changeEmail(
        onSuccess: () -> Unit
    ) {
        if (isEmailValid(newEmail.value)) {
            requester.sendRequest(
                request = {
                    requester.changeEmail(
                        newEmail = newEmail.value
                    )
                },
                onSuccess = {
                    localUser.email = newEmail.value
                    onSuccess.invoke()
                },
                onFailure = { showSnack(it) }
            )
        } else
            newEmailError.value = true
    }

    fun changePassword(
        onSuccess: () -> Unit
    ) {
        if (isPasswordValid(newPassword.value)) {
            requester.sendRequest(
                request = {
                    requester.changePassword(
                        newPassword = newPassword.value
                    )
                },
                onSuccess = { onSuccess.invoke() },
                onFailure = { showSnack(it) }
            )
        } else
            newPasswordError.value = true
    }

    fun changeLanguage(
        newLanguage: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changeLanguage(
                    newLanguage = newLanguage
                )
            },
            onSuccess = {
                localUser.language = newLanguage
                onSuccess.invoke()
            },
            onFailure = { showSnack(it) }
        )
    }

    fun changeCurrency(
        newCurrency: NeutronCurrency,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changeCurrency(
                    newCurrency = newCurrency
                )
            },
            onSuccess = {
                localUser.currency = newCurrency
                onSuccess.invoke()
            },
            onFailure = { showSnack(it) }
        )
    }

    fun changeTheme(
        newTheme: ApplicationTheme,
        onChange: () -> Unit
    ) {
        localUser.theme = newTheme
        onChange.invoke()
    }

    fun deleteAccount(
        onDelete: () -> Unit
    ) {
        requester.sendRequest(
            request = { requester.deleteAccount() },
            onSuccess = {
                clearSession(
                    onClear = onDelete
                )
            },
            onFailure = { showSnack(it) }
        )
    }

    fun clearSession(
        onClear: () -> Unit
    ) {
        localUser.clear()
        onClear.invoke()
    }

}