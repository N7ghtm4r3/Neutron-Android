package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily

class ConnectActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeutronTheme {
                Scaffold {
                    Column (
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Column (
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(
                                    all = 16.dp
                                )
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.hello),
                                fontSize = 45.sp,
                                fontFamily = displayFontFamily
                            )
                        }
                        Column (
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val name = remember { mutableStateOf("") }
                            NeutronOutlinedTextField(
                                value = name,
                                label = R.string.name
                            )
                            val surname = remember { mutableStateOf("") }
                            NeutronOutlinedTextField(
                                value = surname,
                                label = R.string.surname
                            )
                        }
                    }
                }
            }
        }
    }

}