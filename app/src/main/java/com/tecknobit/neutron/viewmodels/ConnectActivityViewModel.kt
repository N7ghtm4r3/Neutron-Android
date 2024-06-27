package com.tecknobit.neutron.viewmodels

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.intl.Locale.Companion.current
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutroncore.helpers.Endpoints.BASE_ENDPOINT
import com.tecknobit.neutroncore.helpers.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.neutroncore.helpers.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isHostValid
import com.tecknobit.neutroncore.helpers.InputValidator.isNameValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.helpers.InputValidator.isServerSecretValid
import com.tecknobit.neutroncore.helpers.InputValidator.isSurnameValid
import com.tecknobit.neutroncore.records.User.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.User.LANGUAGE_KEY
import com.tecknobit.neutroncore.records.User.NAME_KEY
import com.tecknobit.neutroncore.records.User.SURNAME_KEY
import com.tecknobit.neutroncore.records.User.TOKEN_KEY

/**
 * The **ConnectActivityViewModel** class is the support class used by the [ConnectActivityViewModel]
 * to execute the authentication requests to the backend
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 * @param context: the current context where this model has been created
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class ConnectActivityViewModel(
    snackbarHostState: SnackbarHostState,
    val context: Context
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **isSignUp** -> whether the auth request to execute is sign up or sign in
     */
    lateinit var isSignUp: MutableState<Boolean>

    /**
     * **host** -> the value of the host to reach
     */
    lateinit var host: MutableState<String>

    /**
     * **hostError** -> whether the [host] field is not valid
     */
    lateinit var hostError: MutableState<Boolean>

    /**
     * **serverSecret** -> the value of the server secret
     */
    lateinit var serverSecret: MutableState<String>

    /**
     * **serverSecretError** -> whether the [serverSecret] field is not valid
     */
    lateinit var serverSecretError: MutableState<Boolean>

    /**
     * **name** -> the name of the user
     */
    lateinit var name: MutableState<String>

    /**
     * **nameError** -> whether the [name] field is not valid
     */
    lateinit var nameError: MutableState<Boolean>

    /**
     * **surname** -> the surname of the user
     */
    lateinit var surname: MutableState<String>

    /**
     * **surnameError** -> whether the [surname] field is not valid
     */
    lateinit var surnameError: MutableState<Boolean>

    /**
     * **email** -> the email of the user
     */
    lateinit var email: MutableState<String>

    /**
     * **emailError** -> whether the [email] field is not valid
     */
    lateinit var emailError: MutableState<Boolean>

    /**
     * **password** -> the password of the user
     */
    lateinit var password: MutableState<String>

    /**
     * **passwordError** -> whether the [password] field is not valid
     */
    lateinit var passwordError: MutableState<Boolean>

    /**
     * Wrapper function to execute the specific authentication request
     *
     * No-any params required
     */
    fun auth() {
        if (isSignUp.value)
            signUp()
        else
            signIn()
    }

    /**
     * Function to execute the sign up authentication request, if successful the [localUser] will
     * be initialized with the data received by the request
     *
     * No-any params required
     */
    private fun signUp() {
        if (signUpFormIsValid()) {
            val currentLanguageTag = current.toLanguageTag().substringBefore("-")
            var language = LANGUAGES_SUPPORTED[currentLanguageTag]
            language = if (language == null)
                DEFAULT_LANGUAGE
            else
                currentLanguageTag
            requester.changeHost(host.value + BASE_ENDPOINT)
            requester.sendRequest(
                request = {
                    requester.signUp(
                        serverSecret = serverSecret.value,
                        name = name.value,
                        surname = surname.value,
                        email = email.value,
                        password = password.value,
                        language = language
                    )
                },
                onSuccess = { response ->
                    launchApp(
                        name = name.value,
                        surname = surname.value,
                        language = language,
                        response = response
                    )
                },
                onFailure = { showSnack(it) }
            )
        }
    }

    /**
     * Function to validate the inputs for the [signUp] request
     *
     * No-any params required
     *
     * @return whether the inputs are valid as [Boolean]
     */
    private fun signUpFormIsValid(): Boolean {
        var isValid: Boolean = isHostValid(host.value)
        if (!isValid) {
            hostError.value = true
            return false
        }
        isValid = isServerSecretValid(serverSecret.value)
        if (!isValid) {
            serverSecretError.value = true
            return false
        }
        isValid = isNameValid(name.value)
        if (!isValid) {
            nameError.value = true
            return false
        }
        isValid = isSurnameValid(surname.value)
        if (!isValid) {
            surnameError.value = true
            return false
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    /**
     * Function to execute the sign in authentication request, if successful the [localUser] will
     * be initialized with the data received by the request
     *
     * No-any params required
     */
    private fun signIn() {
        if (signInFormIsValid()) {
            requester.changeHost(host.value + BASE_ENDPOINT)
            requester.sendRequest(
                request = {
                    requester.signIn(
                        email = email.value,
                        password = password.value
                    )
                },
                onSuccess = { response ->
                    launchApp(
                        name = response.getString(NAME_KEY),
                        surname = response.getString(SURNAME_KEY),
                        language = response.getString(LANGUAGE_KEY),
                        response = response
                    )
                },
                onFailure = { showSnack(it) }
            )
        }
    }

    /**
     * Function to validate the inputs for the [signIn] request
     *
     * No-any params required
     *
     * @return whether the inputs are valid as [Boolean]
     */
    private fun signInFormIsValid(): Boolean {
        var isValid: Boolean = isHostValid(host.value)
        if (!isValid) {
            hostError.value = true
            return false
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    /**
     * Function to launch the application after the authentication request
     *
     * @param response: the response of the authentication request
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param language: the language of the user
     */
    private fun launchApp(
        response: JsonHelper,
        name: String,
        surname: String,
        language: String
    ) {
        requester.setUserCredentials(
            userId = response.getString(IDENTIFIER_KEY),
            userToken = response.getString(TOKEN_KEY)
        )
        localUser.insertNewUser(
            host.value.ifEmpty { null },
            name,
            surname,
            email.value,
            password.value,
            language,
            response
        )
        context.startActivity(Intent(context, MainActivity::class.java))
    }

}