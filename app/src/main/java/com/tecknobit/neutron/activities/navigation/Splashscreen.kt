package com.tecknobit.neutron.activities.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings.ACTION_BIOMETRIC_ENROLL
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.auth.ConnectActivity
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutron.helpers.AndroidLocalUser
import com.tecknobit.neutron.helpers.AndroidNeutronRequester
import com.tecknobit.neutron.helpers.BiometricPromptManager
import com.tecknobit.neutron.helpers.BiometricPromptManager.BiometricResult.AuthenticationNotSet
import com.tecknobit.neutron.helpers.BiometricPromptManager.BiometricResult.AuthenticationSuccess
import com.tecknobit.neutron.ui.ErrorUI
import com.tecknobit.neutron.ui.PROJECT_LABEL
import com.tecknobit.neutron.ui.theme.AppTypography
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.viewmodels.NeutronViewModel.Companion.requester
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@SuppressLint("CustomSplashScreen")
class Splashscreen : AppCompatActivity(), ImageLoaderFactory {

    companion object {

        lateinit var localUser: AndroidLocalUser

    }

    /**
     * **appUpdateManager** the manager to check if there is an update available
     */
    private lateinit var appUpdateManager: AppUpdateManager

    /**
     * **launcher** the result registered for [appUpdateManager] and the action to execute if fails
     */
    private var launcher  = registerForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode != RESULT_OK)
            launchApp(MainActivity::class.java)
    }

    /**
     * **biometricPromptManager** the manager used to authenticate with the bio params
     */
    private val biometricPromptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localUser = AndroidLocalUser(this)
        setContent {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
            Coil.imageLoader(applicationContext)
            Coil.setImageLoader(newImageLoader())
            PROJECT_LABEL = RevenueLabel(
                getString(R.string.project),
                ProjectRevenue.PROJECT_LABEL_COLOR
            )
            NeutronTheme (
                darkTheme = false
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text (
                        text = getString(R.string.app_name),
                        color = Color.White,
                        style = AppTypography.displayLarge,
                        fontSize = 55.sp,
                    )
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "by Tecknobit",
                            color = Color.White,
                            fontFamily = displayFontFamily,
                            fontSize = 14.sp,
                        )
                    }
                }
                LaunchBiometricAuth()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    /**
     * Function to launch the biometric check with the [biometricPromptManager]
     *
     * No-any params required
     */
    @Composable
    private fun LaunchBiometricAuth() {
        val biometricResult by biometricPromptManager.promptResults.collectAsState(
            initial = null
        )
        val enrollLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {}
        )
        LaunchedEffect(biometricResult) {
            if(biometricResult is AuthenticationNotSet) {
                val enrollIntent = Intent(ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
        if(biometricResult == null) {
            biometricPromptManager.showBiometricPrompt(
                title = stringResource(R.string.login_required),
                description = stringResource(R.string.enter_your_credentials_to_continue)
            )
        }
        biometricResult?.let { result ->
            when(result) {
                AuthenticationSuccess -> {
                    val firstScreen = if (localUser.isAuthenticated)
                        MainActivity::class.java
                    else
                        ConnectActivity::class.java
                    requester = AndroidNeutronRequester(
                        host = localUser.hostAddress,
                        userId = localUser.userId,
                        userToken = localUser.userToken
                    )
                    checkForUpdates(firstScreen)
                }
                else -> {
                    NeutronTheme {
                        ErrorUI(
                            retryAction = { LaunchBiometricAuth() }
                        )
                    }
                }
            }
        }
    }

    /**
     * Method to check if there are some update available to install
     *
     * @param intentDestination: the intent to reach
     */
    private fun checkForUpdates(
        intentDestination: Class<*>
    ) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UPDATE_AVAILABLE
            val isUpdateSupported = info.isImmediateUpdateAllowed
            if(isUpdateAvailable && isUpdateSupported) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    launcher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            } else
                launchApp(intentDestination)
        }.addOnFailureListener {
            launchApp(intentDestination)
        }
    }

    /**
     * Method to launch the app and the user session
     *
     * @param intentDestination: the intent to reach
     *
     */
    private fun launchApp(
        intentDestination: Class<*>
    ) {
        startActivity(Intent(this@Splashscreen, intentDestination))
    }

    /**
     * Return a new [ImageLoader].
     */
    override fun newImageLoader(): ImageLoader {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
        return ImageLoader.Builder(applicationContext)
            .okHttpClient {
                OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.socketFactory,
                        validateSelfSignedCertificate()[0] as X509TrustManager
                    )
                    .hostnameVerifier { _: String?, _: SSLSession? -> true }
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .build()
            }
            .addLastModifiedToFileCacheKey(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    /**
     * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
     * No-any params required
     *
     * @return list of trust managers as [Array] of [TrustManager]
     * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to
     * use for test only or in a private distribution on own infrastructure
     */
    private fun validateSelfSignedCertificate(): Array<TrustManager> {
        return arrayOf(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

                @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}

                @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
        })
    }

}