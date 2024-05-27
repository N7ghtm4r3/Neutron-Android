package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SpeakerNotesOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.tecknobit.neutron.activities.NeutronActivity
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.PROJECT_LABEL
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.activities.session.addactivities.AddRevenuesActivity
import com.tecknobit.neutron.ui.EmptyListUI
import com.tecknobit.neutron.ui.GeneralRevenue
import com.tecknobit.neutron.ui.LabelBadge
import com.tecknobit.neutron.ui.RevenueInfo
import com.tecknobit.neutron.ui.SwipeToDeleteContainer
import com.tecknobit.neutron.ui.getWalletBalance
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.bodyFontFamily
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.revenues.InitialRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.TicketRevenue

class MainActivity : NeutronActivity() {

    companion object {

        val revenues = mutableStateListOf<Revenue>()

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: LOAD CORRECTLY
        if(revenues.isEmpty()) {
            revenues.add(
                ProjectRevenue(
                    "gag",
                    "Prova",
                    System.currentTimeMillis(),
                    InitialRevenue(
                        "gaga",
                        2000.0,
                        System.currentTimeMillis()
                    ),
                    listOf(
                        TicketRevenue(
                            "g11aga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga",
                            1715893715000L
                        ),
                        TicketRevenue(
                            "g1aga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga",
                            System.currentTimeMillis()
                        ),
                        TicketRevenue(
                            "gaga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga",
                            System.currentTimeMillis()
                        ),
                        TicketRevenue(
                            "25gaga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga",
                            System.currentTimeMillis()
                        ),
                        TicketRevenue(
                            "24gaga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga"
                        ),
                        TicketRevenue(
                            "4gaga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga",
                            System.currentTimeMillis()
                        ),
                        TicketRevenue(
                            "3gaga",
                            "Ciao",
                            1000.0,
                            System.currentTimeMillis(),
                            "gaaga"
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
                        RevenueLabel(
                            "ff",
                            "Prog",
                            "#33A396"
                        ),
                        RevenueLabel(
                            "sff",
                            "Proggag",
                            "#8BAEA2"
                        ),
                        RevenueLabel(
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
        }
        setContent {
            // TODO: USE THE REAL DATA
            val walletTrendPercent by remember { mutableDoubleStateOf(1.0) }
            NeutronTheme {
                Scaffold (
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
                                            text = "${revenues.getWalletBalance()}${user.currency.symbol}",
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
                                            .data(user.profilePic)
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
                                            SwipeToDeleteContainer(
                                                item = revenue,
                                                onDelete = {
                                                    // TODO: MAKE REQUEST THEN
                                                    revenues.remove(revenue)
                                                }
                                            ) {
                                                GeneralRevenue(
                                                    revenue = revenue
                                                )
                                            }
                                        } else {
                                            SwipeToDeleteContainer(
                                                item = revenue,
                                                onDelete = {
                                                    // TODO: MAKE REQUEST THEN
                                                    revenues.remove(revenue)
                                                }
                                            ) {
                                                ProjectRevenue(
                                                    revenue = revenue as ProjectRevenue
                                                )
                                            }
                                        }
                                        HorizontalDivider()
                                    }
                                }
                            } else {
                                EmptyListUI(
                                    icon = Icons.Default.SpeakerNotesOff,
                                    subText = R.string.no_revenues_yet
                                )
                            }
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

    @Composable
    private fun ProjectRevenue(
        revenue: ProjectRevenue
    ) {
        val navToProject = {
            val intent = Intent(this@MainActivity, ProjectRevenueActivity::class.java)
            intent.putExtra(IDENTIFIER_KEY, revenue.id)
            startActivity(intent)
        }
        ListItem(
            modifier = Modifier
                .clickable {
                    navToProject.invoke()
                },
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
                        onClick = { navToProject.invoke() }
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

}