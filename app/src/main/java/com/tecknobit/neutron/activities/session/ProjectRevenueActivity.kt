package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.session.MainActivity.Companion.revenues
import com.tecknobit.neutron.ui.getProjectRevenue
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

class ProjectRevenueActivity : NeutronActivity() {

    private lateinit var projectRevenue: MutableState<ProjectRevenue>

    // TODO: USE THE REAL DATA
    val currency = "€"

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
                            cardContent = {
                                IconButton(
                                    onClick = {
                                        startActivity(Intent(this@ProjectRevenueActivity,
                                            MainActivity::class.java))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
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

                            }
                        )
                    }
                } else {
                    // TODO: MAKE THE ERROR UI
                }
            }
        }
    }

}