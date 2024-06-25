package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.androidLocalServer
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.helpers.AndroidNeutronRequester
import com.tecknobit.neutroncore.helpers.InputValidator.isEmailValid
import com.tecknobit.neutroncore.helpers.InputValidator.isHostValid
import com.tecknobit.neutroncore.helpers.InputValidator.isPasswordValid
import com.tecknobit.neutroncore.helpers.InputValidator.isServerSecretValid
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import com.tecknobit.neutroncore.records.User.UserStorage
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import com.tecknobit.neutroncore.records.User.UserStorage.Online
import com.tecknobit.neutroncore.records.revenues.Revenue
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

    lateinit var hostAddress: MutableState<String>

    lateinit var hostError: MutableState<Boolean>

    lateinit var serverSecret: MutableState<String>

    lateinit var serverSecretError: MutableState<Boolean>

    lateinit var isExecuting: MutableState<Boolean>

    lateinit var waiting: MutableState<Boolean>

    lateinit var success: MutableState<Boolean>

    fun changeProfilePic(
        imagePath: String,
        profilePic: MutableState<String>
    ) {
        val onSuccess: (JsonHelper) -> Unit = {
            profilePic.value = imagePath
            localUser.setLocalProfilePicPath(imagePath)
            localUser.profilePic = it.getString(PROFILE_PIC_KEY)
        }
        if (workInLocal()) {
            androidLocalServer.changeProfilePic(
                newProfilePic = imagePath,
                onSuccess = onSuccess,
                onFailure = { showSnack(it) }
            )
        } else {
            requester.sendRequest(
                request = {
                    requester.changeProfilePic(
                        profilePic = File(imagePath)
                    )
                },
                onSuccess = onSuccess,
                onFailure = { showSnack(it) }
            )
        }
    }

    fun changeEmail(
        onSuccess: () -> Unit
    ) {
        if (isEmailValid(newEmail.value)) {
            val successAction = {
                localUser.email = newEmail.value
                onSuccess.invoke()
            }
            if (workInLocal()) {
                androidLocalServer.changeEmail(
                    newEmail = newEmail.value,
                    onSuccess = { successAction.invoke() },
                    onFailure = { showSnack(it) }
                )
            } else {
                requester.sendRequest(
                    request = {
                        requester.changeEmail(
                            newEmail = newEmail.value
                        )
                    },
                    onSuccess = { successAction.invoke() },
                    onFailure = { showSnack(it) }
                )
            }
        } else
            newEmailError.value = true
    }

    fun changePassword(
        onSuccess: () -> Unit
    ) {
        if (isPasswordValid(newPassword.value)) {
            if (workInLocal()) {
                androidLocalServer.changePassword(
                    newPassword = newPassword.value,
                    onSuccess = { onSuccess.invoke() },
                    onFailure = { showSnack(it) }
                )
            } else {
                requester.sendRequest(
                    request = {
                        requester.changePassword(
                            newPassword = newPassword.value
                        )
                    },
                    onSuccess = { onSuccess.invoke() },
                    onFailure = { showSnack(it) }
                )
            }
        } else
            newPasswordError.value = true
    }

    fun changeLanguage(
        newLanguage: String,
        onSuccess: () -> Unit
    ) {
        val successAction = {
            localUser.language = newLanguage
            onSuccess.invoke()
        }
        if (workInLocal()) {
            androidLocalServer.changeLanguage(
                newLanguage = newLanguage,
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        } else {
            requester.sendRequest(
                request = {
                    requester.changeLanguage(
                        newLanguage = newLanguage
                    )
                },
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        }
    }

    fun changeCurrency(
        newCurrency: NeutronCurrency,
        onSuccess: () -> Unit
    ) {
        val successAction = {
            localUser.currency = newCurrency
            onSuccess.invoke()
        }
        if (workInLocal()) {
            androidLocalServer.changeCurrency(
                newCurrency = newCurrency,
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        } else {
            requester.sendRequest(
                request = {
                    requester.changeCurrency(
                        newCurrency = newCurrency
                    )
                },
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        }
    }

    fun changeTheme(
        newTheme: ApplicationTheme,
        onChange: () -> Unit
    ) {
        localUser.theme = newTheme
        onChange.invoke()
    }

    fun changeStorage() {
        success.value = false
        if(workInLocal())
            transferIn()
        else
            transferOut()
    }

    // TODO: TO TEST CORRECTLY
    private fun transferIn() {
        if(!isHostValid(hostAddress.value))
            hostError.value = true
        else if(!isServerSecretValid(serverSecret.value))
            serverSecretError.value = true
        else {
            val requester = AndroidNeutronRequester(
                host = hostAddress.value
            )
            val revenues = getLocalRevenuesStored()
            if(revenues != null) {
                requester.sendRequest(
                    request = {
                        requester.transferIn(
                            serverSecret = serverSecret.value,
                            user = localUser.toUser(),
                            revenues = revenues
                        )
                    },
                    onSuccess = {
                        localUser.profilePic = it.getString(PROFILE_PIC_KEY)
                        transferSuccessful(
                            storage = Online
                        )
                    },
                    onFailure = {
                        transferFailed(
                            response = it
                        )
                    }
                )
            }
        }
    }

    private fun getLocalRevenuesStored() : List<Revenue>? {
        // TODO: FETCH FROM THE LOCAL DATABASE THEN
        
        // TODO: INSERT IN THE LOOP
        if(!isExecuting.value)
            return null
        
        return emptyList()
    }

    private fun transferOut() {
        requester.sendRequest(
            request = { requester.transferOut() },
            onSuccess = {
                // TODO: SAVE REVENUES IN LOCAL
                requester.setUserCredentials(
                    userId = null,
                    userToken = null
                )
                localUser.profilePic = null
                transferSuccessful(
                    storage = Local
                )
            },
            onFailure = {
                transferFailed(
                    response = it
                )
            }
        )
    }

    private fun transferSuccessful(
        storage: UserStorage
    ) {
        localUser.storage = storage
        waiting.value = false
        success.value = true
    }

    private fun transferFailed(
        response: JsonHelper
    ) {
        waiting.value = false
        showSnack(response)
    }

    fun deleteAccount(
        onDelete: () -> Unit
    ) {
        val successAction = {
            clearSession(
                onClear = onDelete
            )
        }
        if (workInLocal()) {
            androidLocalServer.deleteAccount(
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        } else {
            requester.sendRequest(
                request = { requester.deleteAccount() },
                onSuccess = { successAction.invoke() },
                onFailure = { showSnack(it) }
            )
        }
    }

    fun clearSession(
        onClear: () -> Unit
    ) {
        localUser.clear()
        onClear.invoke()
    }

}