package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.activities.session.MainActivity.Companion.revenues
import com.tecknobit.neutron.activities.session.addactivities.AddTicketActivity
import com.tecknobit.neutron.ui.DisplayTickets
import com.tecknobit.neutron.ui.ErrorUI
import com.tecknobit.neutron.ui.NeutronAlertDialog
import com.tecknobit.neutron.ui.getProjectRevenue
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.viewmodels.ProjectRevenueActivityViewModel
import com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

/**
 * The **ProjectRevenueActivity** class is the activity where the user can show a project with its
 * details and ticket
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronActivity
 * @see ComponentActivity
 */
class ProjectRevenueActivity : NeutronActivity() {

    /**
     * *projectRevenue* -> the current project revenue displayed
     */
    private lateinit var projectRevenue: State<ProjectRevenue?>

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel: ProjectRevenueActivityViewModel = ProjectRevenueActivityViewModel(
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
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentProjectRevenue = revenues.value!!.getProjectRevenue(
                intent.getStringExtra(GeneralRevenue.IDENTIFIER_KEY)!!
            )
            viewModel.showDeleteProject = remember { mutableStateOf(false) }
            NeutronTheme {
                if(currentProjectRevenue != null) {
                    viewModel.setInitialProjectRevenue(currentProjectRevenue)
                    viewModel.getProjectRevenue()
                    projectRevenue = viewModel.projectRevenue.observeAsState()
                    Scaffold (
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                navigationIcon = {
                                    IconButton(
                                        onClick = { navBack() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                title = {},
                                actions = {
                                    IconButton(
                                        onClick = { viewModel.showDeleteProject.value = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    DeleteProjectRevenue()
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    val intent = Intent(this, AddTicketActivity::class.java)
                                    intent.putExtra(IDENTIFIER_KEY, currentProjectRevenue.id)
                                    startActivity(intent)
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
                            modifier = Modifier
                                .padding(
                                    top = it.calculateTopPadding()
                                ),
                            cardHeight = 85.dp,
                            cardContent = {
                                Text(
                                    text = projectRevenue.value!!.title,
                                    fontFamily = displayFontFamily,
                                    color = Color.White,
                                    fontSize = 25.sp
                                )
                                Text(
                                    text = stringResource(
                                        R.string.total_revenues,
                                        projectRevenue.value!!.value,
                                        localUser.currency.symbol
                                    ),
                                    color = Color.White
                                )
                            },
                            uiContent = {
                                DisplayTickets(
                                    projectRevenue = projectRevenue.value!!,
                                    onRight = { ticket ->
                                        viewModel.closeTicket(
                                            ticket = ticket
                                        )
                                    },
                                    onDelete = { ticket ->
                                        viewModel.deleteTicket(
                                            ticket = ticket
                                        )
                                    }
                                )
                            }
                        )
                    }
                } else
                    ErrorUI()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navBack()
            }
        })
    }

    /**
     * Function to display the section to delete the current project displayed
     *
     * No-any params required
     */
    @Composable
    private fun DeleteProjectRevenue() {
        if (viewModel.showDeleteProject.value)
            viewModel.suspendRefresher()
        NeutronAlertDialog(
            icon = Icons.Default.Delete,
            onDismissAction = {
                viewModel.showDeleteProject.value = false
                viewModel.restartRefresher()
            },
            show = viewModel.showDeleteProject,
            title = R.string.delete_project,
            text = R.string.delete_project_warn_text,
            confirmAction = {
                viewModel.deleteProjectRevenue {
                    navBack()
                }
            }
        )
    }

    /**
     * Function to execute the back navigation from the current activity to the previous activity
     *
     * No-any params required
     */
    private fun navBack() {
        startActivity(Intent(this@ProjectRevenueActivity, MainActivity::class.java))
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