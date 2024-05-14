package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.PROJECT_LABEL
import com.tecknobit.neutron.ui.backgroundColor
import com.tecknobit.neutron.ui.getWalletBalance
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.bodyFontFamily
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.Label
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue

class MainActivity : ComponentActivity() {

    companion object {

        val revenues = mutableStateListOf<Revenue>()

        // TODO: USE THE REAL DATA
        val currency = "€"

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: LOAD CORRECTLY
        revenues.add(
            ProjectRevenue(
                "gag",
                "Prova",
                System.currentTimeMillis(),
                GeneralRevenue(
                    "gaga",
                    "Entry",
                    2000.0,
                    System.currentTimeMillis(),
                    emptyList(),
                    null
                ),
                listOf(
                    ProjectRevenue.Ticket(
                        "gaga",
                        1000.0,
                        1222222L,
                        System.currentTimeMillis()
                    )
                )
            )
        )
        revenues.add(
            GeneralRevenue(
                "aaa",
                "General",
                100000.0,
                System.currentTimeMillis(),
                listOf(
                    Label(
                        "ff",
                        "Prog",
                        "#33A396"
                    ),
                    Label(
                        "sff",
                        "Proggag",
                        "#8BAEA2"
                    ),
                    Label(
                        "sffa",
                        "cfnafna",
                        "#59EC21"
                    )
                ),
                "Lorem Ipsum è un testo segnaposto utilizzato nel settore della tipografia e della stampa. Lorem Ipsum è considerato il testo segnaposto standard sin dal sedicesimo secolo, quando un anonimo tipografo prese una cassetta di caratteri e li assemblò per preparare un testo campione. È sopravvissuto non solo a più di cinque secoli, ma anche al passaggio alla videoimpaginazione, pervenendoci sostanzialmente inalterato. Fu reso popolare, negli anni ’60, con la diffusione dei fogli di caratteri trasferibili “Letraset”, che contenevano passaggi del Lorem Ipsum, e più recentemente da software di impaginazione come Aldus PageMaker, che includeva versioni del Lorem Ipsum."
            )
        )
        revenues.add(
            GeneralRevenue(
                "aaaa",
                "General",
                2000.0,
                System.currentTimeMillis(),
                emptyList(),
                "Prova\nagag\naagagaga\nanan\n"
            )
        )
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
                                            Text(
                                                text = "${revenues.getWalletBalance()}$currency",
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
                        if(revenues.isNotEmpty()) {
                            LazyColumn (
                                modifier = Modifier
                                    .fillMaxHeight(),
                                contentPadding = PaddingValues(
                                    bottom = 16.dp
                                )
                            ) {
                                items(
                                    items = revenues,
                                    key = { it.id }
                                ) { revenue ->
                                    if(revenue is GeneralRevenue) {
                                        GeneralRevenue(
                                            revenue = revenue
                                        )
                                    } else {
                                        ProjectRevenue(
                                            revenue = revenue as ProjectRevenue
                                        )
                                    }
                                    HorizontalDivider()
                                }
                            }
                        } else {
                            // TODO: MAKE THE LIST EMPTY UI
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

    @Composable
    private fun GeneralRevenue(
        revenue: GeneralRevenue
    ) {
        var descriptionDisplayed by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = {
                Text(
                    text = revenue.title,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                RevenueInfo(
                    revenue = revenue
                )
            },
            trailingContent = {
                Column {
                    LazyRow (
                        modifier = Modifier
                            .widthIn(
                                max = 100.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(
                            items = revenue.labels,
                            key = { it.id }
                        ) { label ->
                            LabelBadge(
                                label = label
                            )
                        }
                    }
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = { descriptionDisplayed = !descriptionDisplayed }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
                            imageVector = if(descriptionDisplayed)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        AnimatedVisibility(
            visible = descriptionDisplayed
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                text = revenue.description,
                textAlign = TextAlign.Justify
            )
        }
    }

    @Composable
    private fun ProjectRevenue(
        revenue: ProjectRevenue
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = revenue.title,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                RevenueInfo(
                    revenue = revenue
                )
            },
            trailingContent = {
                Column {
                    LabelBadge(
                        label = PROJECT_LABEL
                    )
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {
                            // TODO: NAV TO DEDICATED PAGE
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun RevenueInfo(
        revenue: Revenue
    ) {
        Column {
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.revenue)
                )
                Text(
                    text = "${revenue.value}$currency",
                    fontFamily = displayFontFamily
                )
            }
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.date)
                )
                Text(
                    text = revenue.revenueDate,
                    fontFamily = displayFontFamily
                )
            }
        }
    }

    @Composable
    private fun LabelBadge(
        label: Label
    ) {
        Card (
            colors = CardDefaults.cardColors(
                containerColor = label.color.backgroundColor()
            ),
            shape = RoundedCornerShape(
                size = 5.dp
            )
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        all = 5.dp
                    ),
                text = label.text
            )
        }
    }

}