package com.tecknobit.neutron.viewmodels

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.text.intl.Locale.Companion.current
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutroncore.helpers.Endpoints.BASE_ENDPOINT
import com.tecknobit.neutroncore.helpers.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.neutroncore.helpers.InputValidator.LANGUAGES_SUPPORTED

class ConnectActivityViewModel(
    snackbarHostState: SnackbarHostState,
    private val context: Context
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    fun signUp(
        hostAddress: String? = null,
        serverSecret: String? = null,
        name: String,
        surname: String,
        email: String,
        password: String,
    ) {
        if (hostAddress != null) {
            requester.changeHost(hostAddress + BASE_ENDPOINT)
            requester.sendRequest(
                request = {
                    var language = LANGUAGES_SUPPORTED[current.toLanguageTag().substringBefore("-")]
                    if (language == null)
                        language = DEFAULT_LANGUAGE
                    requester.signUp(
                        serverSecret = serverSecret!!,
                        name = name,
                        surname = surname,
                        email = email,
                        password = password,
                        language = language
                    )
                },
                onSuccess = {
                    // TODO: STORE IN LOCAL THE DATA THEN
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                onFailure = { showSnack(it) }
            )
        } else {
            // TODO: LOCAL SIGN UP
        }
    }

    fun signIn(
        hostAddress: String? = null,
        email: String,
        password: String
    ) {

    }

}