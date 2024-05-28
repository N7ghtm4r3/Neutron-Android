package com.tecknobit.neutron.activities.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.session.ConnectActivity
import com.tecknobit.neutron.ui.PROJECT_LABEL
import com.tecknobit.neutron.ui.theme.AppTypography
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.User
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class Splashscreen : ComponentActivity() {

    companion object {

        // TODO: TO INIT CORRECTLY
        val user = User()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                LaunchedEffect(key1 = true) {
                    delay(250)
                    // TODO: MAKE THE REAL NAVIGATION
                    startActivity(Intent(this@Splashscreen, ConnectActivity::class.java))
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

}