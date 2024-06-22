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

class ProjectRevenueActivity : NeutronActivity() {

    private lateinit var projectRevenue: State<ProjectRevenue?>

    private val viewModel: ProjectRevenueActivityViewModel = ProjectRevenueActivityViewModel(
        snackbarHostState = snackbarHostState
    )

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

    private fun navBack() {
        startActivity(Intent(this@ProjectRevenueActivity, MainActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}