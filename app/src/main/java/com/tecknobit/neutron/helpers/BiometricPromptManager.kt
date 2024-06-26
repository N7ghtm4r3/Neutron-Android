package com.tecknobit.neutron.helpers

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.from
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * The **BiometricPromptManager** class is useful to manage the biometric authentication
 *
 * @param activity: the activity from launch the biometric authentication
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class BiometricPromptManager(
    private val activity: AppCompatActivity
) {

    /**
     * **resultChannel** -> the channel where the result applied
     */
    private val resultChannel = Channel<BiometricResult>()

    /**
     * **promptResults** -> the prompts used to apply the result
     */
    val promptResults = resultChannel.receiveAsFlow()

    /**
     * Function to show the biometric authentication prompt
     *
     * @param title: the title of the prompt
     * @param description: the description of the prompt
     */
    fun showBiometricPrompt(
        title: String,
        description: String
    ) {
        val manager = from(activity)
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
        when(manager.canAuthenticate(authenticators)) {
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            else -> Unit
        }
        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )
        prompt.authenticate(promptInfo.build())
    }

    /**
     * The [BiometricResult] interface is used to manage the result of the biometric authentication
     */
    sealed interface BiometricResult {

        /**
         * The [HardwareUnavailable] result occurred when the hardware is not available
         */
        data object HardwareUnavailable: BiometricResult

        /**
         * The [FeatureUnavailable] result occurred when the feature is not available
         */
        data object FeatureUnavailable: BiometricResult

        /**
         * The [AuthenticationError] result occurred when the authentication failed
         *
         * @param error: the error occurred
         */
        data class AuthenticationError(val error: String): BiometricResult

        /**
         * The [AuthenticationFailed] result occurred when the authentication failed
         */
        data object AuthenticationFailed: BiometricResult

        /**
         * The [AuthenticationSuccess] result occurred when the authentication has been successful
         */
        data object AuthenticationSuccess: BiometricResult

        /**
         * The [AuthenticationNotSet] result occurred when the authentication is not set
         */
        data object AuthenticationNotSet: BiometricResult

    }

}