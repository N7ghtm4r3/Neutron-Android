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

/**
 * The **MainActivity** class is the activity where the user can show his/her revenues and create
 * others revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronActivity
 * @see ComponentActivity
 */
class MainActivity : NeutronActivity() {

    companion object {

        /**
         * **revenues** the current list of the user's revenues
         */
        lateinit var revenues: State<MutableList<Revenue>?>

    }

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = MainActivityViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * On create method
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * If your ComponentActivity is annotated with {@link ContentView}, this will
     * call {@link #setContentView(int)} for you.
     */
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

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or {@link #onPause}. This
     * is usually a hint for your activity to start interacting with the user, which is a good
     * indicator that the activity became active and ready to receive input. This sometimes could
     * also be a transit state toward another resting state. For instance, an activity may be
     * relaunched to {@link #onPause} due to configuration changes and the activity was visible,
     * but wasn’t the top-most activity of an activity task. {@link #onResume} is guaranteed to be
     * called before {@link #onPause} in this case which honors the activity lifecycle policy and
     * the activity eventually rests in {@link #onPause}.
     *
     * <p>On platform versions prior to {@link android.os.Build.VERSION_CODES#Q} this is also a good
     * place to try to open exclusive-access devices or to get access to singleton resources.
     * Starting  with {@link android.os.Build.VERSION_CODES#Q} there can be multiple resumed
     * activities in the system simultaneously, so {@link #onTopResumedActivityChanged(boolean)}
     * should be used for that purpose instead.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * Will be set the **[FetcherManager.activeContext]** with the current context
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     * @see #onTopResumedActivityChanged(boolean)
     */
    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}