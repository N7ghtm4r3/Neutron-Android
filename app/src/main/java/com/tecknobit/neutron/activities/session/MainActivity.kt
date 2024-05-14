package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.neutron.R
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.bodyFontFamily
import com.tecknobit.neutron.ui.theme.displayFontFamily

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO: USE THE REAL DATA
            val walletTrendPercent by remember { mutableDoubleStateOf(1.0) }
            NeutronTheme {
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
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Card (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(
                                size = 0.dp
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        all = 16.dp
                                    )
                            ) {
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
                                            // TODO: USE THE REAL DATA
                                            val currency = "€"
                                            Text(
                                                text = "566.00$currency",
                                                fontFamily = bodyFontFamily,
                                                fontSize = 35.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${textualizeAssetPercent(walletTrendPercent)}/"
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
                                                    // TODO: OPEN THE PROFILE
                                                }
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                ),
                                            contentScale = ContentScale.Crop,
                                            model = ImageRequest.Builder(this@MainActivity)
                                                .data("https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg")
                                                .crossfade(true)
                                                .crossfade(500)
                                                .build(),
                                                //TODO: USE THE REAL IMAGE ERROR .error(),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                        LazyColumn (
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentPadding = PaddingValues(
                                bottom = 16.dp
                            )
                        ) {
                            items(
                                count = 5
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "gg"
                                        )
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
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