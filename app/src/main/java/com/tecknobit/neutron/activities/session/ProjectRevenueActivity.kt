package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.session.MainActivity.Companion.revenues
import com.tecknobit.neutron.ui.GeneralRevenue
import com.tecknobit.neutron.ui.SwipeToDeleteContainer
import com.tecknobit.neutron.ui.getProjectRevenue
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

class ProjectRevenueActivity : NeutronActivity() {

    private lateinit var projectRevenue: MutableState<ProjectRevenue>

    // TODO: USE THE REAL DATA
    val currency = "€"

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentProjectRevenue = revenues.getProjectRevenue(
                intent.getStringExtra(GeneralRevenue.IDENTIFIER_KEY)!!
            )
            NeutronTheme {
                if(currentProjectRevenue != null) {
                    projectRevenue = remember { mutableStateOf(currentProjectRevenue) }
                    Scaffold (
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
                                        onClick = {
                                            // TODO: MAKE THE REQUEST THEN
                                            navBack()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    // TODO: MAKE THE ACTION
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
                                    text = projectRevenue.value.title,
                                    fontFamily = displayFontFamily,
                                    color = Color.White,
                                    fontSize = 25.sp
                                )
                                Text(
                                    text = stringResource(
                                        R.string.total_revenues,
                                        projectRevenue.value.value,
                                        currency
                                    ),
                                    color = Color.White
                                )
                            },
                            uiContent = {
                                // TODO: USE projectRevenue.value INSTEAD
                                val tickets = projectRevenue.value.tickets.toMutableList()
                                LazyColumn {
                                    item {
                                        GeneralRevenue(
                                            revenue = projectRevenue.value.initialRevenue
                                        )
                                    }
                                    if(tickets.isNotEmpty()) {
                                        items(
                                            key = { ticket -> ticket.id },
                                            items = tickets
                                        ) { ticket ->
                                            SwipeToDeleteContainer(
                                                item = ticket,
                                                onDelete = {
                                                    // TODO: MAKE REQUEST THEN
                                                    tickets.remove(ticket)
                                                }
                                            ) {
                                                GeneralRevenue(
                                                    revenue = ticket
                                                )
                                            }
                                            HorizontalDivider()
                                        }
                                    } else {
                                        // TODO: MAKE THE UI EMPTY LIST
                                    }
                                }
                            }
                        )
                    }
                } else {
                    // TODO: MAKE THE ERROR UI
                }
            }
        }
    }

    private fun navBack() {
        startActivity(Intent(this@ProjectRevenueActivity, MainActivity::class.java))
    }

}