package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.activities.session.addactivities.AddRevenuesActivity
import com.tecknobit.neutron.ui.DisplayRevenues
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.bodyFontFamily
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.viewmodels.MainActivityViewModel
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.revenues.Revenue

class MainActivity : NeutronActivity() {

    companion object {

        lateinit var revenues: State<MutableList<Revenue>?>

    }

    private val viewModel = MainActivityViewModel(
        snackbarHostState = snackbarHostState
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localUser.language))
        setContent {
            viewModel.getRevenuesList()
            revenues = viewModel.revenues.observeAsState()
            val walletBalance = viewModel.walletBalance.observeAsState()
            val walletTrend = viewModel.walletTrend.observeAsState()
            NeutronTheme {
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                startActivity(Intent(this, AddRevenuesActivity::class.java))
                            }
                        ) {
                           Icon(
                               imageVector = Icons.Default.Add,
                               contentDescription = null
                           )
                        }
                    }
                ) {
                    DisplayContent(
                        cardContent = {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column (
                                    modifier = Modifier
                                        .weight(4f)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.earnings),
                                        fontFamily = displayFontFamily,
                                        color = Color.White
                                    )
                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Text(
                                            text = "${walletBalance.value}${localUser.currency.symbol}",
                                            fontFamily = bodyFontFamily,
                                            fontSize = 35.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${walletTrend.value}/"
                                                    + stringResource(R.string.last_month),
                                            fontFamily = bodyFontFamily,
                                            color = Color.White
                                        )
                                    }
                                }
                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        ProfileActivity::class.java
                                                    )
                                                )
                                            }
                                            .border(
                                                width = 1.dp,
                                                color = Color.White,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop,
                                        model = ImageRequest.Builder(this@MainActivity)
                                            .data(localUser.profilePic)
                                            .crossfade(true)
                                            .crossfade(500)
                                            .build(),
                                        //TODO: USE THE REAL IMAGE ERROR .error(),
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        uiContent = {
                            DisplayRevenues(
                                snackbarHostState = snackbarHostState,
                                revenues = revenues.value!!,
                                navToProject = { revenue ->
                                    val intent = Intent(
                                        this@MainActivity,
                                        ProjectRevenueActivity::class.java
                                    )
                                    intent.putExtra(IDENTIFIER_KEY, revenue.id)
                                    startActivity(intent)
                                }
                            )
                        }
                    )
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}