package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.activities.session.MainActivity.Companion.currency
import com.tecknobit.neutron.ui.theme.NeutronTheme

class AddRevenueActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeutronTheme {
                Scaffold (
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            },
                            title = {}
                        )
                    },
                ) {
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            var revenueValue by remember {
                                mutableDoubleStateOf(0.00)
                            }
                            Text(
                                text = "$revenueValue$currency",
                                fontSize = 60.sp
                            )
                        }
                        Card (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 275.dp
                                ),
                            shape = RoundedCornerShape(
                                topStart = 50.dp,
                                topEnd = 50.dp
                            )
                        ) {
                            Keyboard()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Keyboard() {
        Column (
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn (
            ) {
                repeat(3) { j ->
                    item {
                        Row (
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            repeat(3) { i ->
                                NumberKeyboardButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    number = (j * 3) + i + 1
                                )
                            }
                        }
                    }
                }
                item {
                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NumberKeyboardButton(
                            modifier = Modifier
                                .weight(1f),
                            number = 0
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NumberKeyboardButton(
        modifier: Modifier,
        number: Int
    ) {
        TextButton(
            modifier = modifier,
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = number.toString(),
                fontSize = 50.sp
            )
        }
    }

}