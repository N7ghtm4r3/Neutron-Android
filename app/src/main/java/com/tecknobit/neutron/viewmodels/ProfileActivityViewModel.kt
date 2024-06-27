package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import java.io.File

/**
 * The **ProfileActivityViewModel** class is the support class used by the [ProfileActivity] to
 * change the user account settings or preferences
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class ProfileActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **newEmail** -> the value of the new email to set
     */
    lateinit var newEmail: MutableState<String>

    /**
     * **newEmailError** -> whether the [newEmail] field is not valid
     */
    lateinit var newEmailError: MutableState<Boolean>

    /**
     * **newPassword** -> the value of the new password to set
     */
    lateinit var newPassword: MutableState<String>

    /**
     * **newPasswordError** -> whether the [newPassword] field is not valid
     */
    lateinit var newPasswordError: MutableState<Boolean>

    /**
     * Function to execute the profile pic change
     *
     * @param imagePath: the path of the image to set
     * @param profilePic: the state used to display the current profile pic
     */
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

    /**
     * Function to execute the email change
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
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

    /**
     * Function to execute the password change
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
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

    /**
     * Function to execute the language change
     *
     * @param newLanguage: the new language of the user
     * @param onSuccess: the action to execute if the request has been successful
     */
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

    /**
     * Function to execute the currency change
     *
     * @param newCurrency: the new currency of the user
     * @param onSuccess: the action to execute if the request has been successful
     */
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

    /**
     * Function to execute the theme change
     *
     * @param newTheme: the new theme of the user
     * @param onChange: the action to execute when the theme changed
     */
    fun changeTheme(
        newTheme: ApplicationTheme,
        onChange: () -> Unit
    ) {
        localUser.theme = newTheme
        onChange.invoke()
    }

    /**
     * Function to execute the account deletion
     *
     * @param onDelete: the action to execute when the account has been deleted
     */
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

    /**
     * Method to clear the current [localUser] session
     *
     * @param onClear: the action to execute when the session has been cleaned
     */
    fun clearSession(
        onClear: () -> Unit
    ) {
        localUser.clear()
        onClear.invoke()
    }

}