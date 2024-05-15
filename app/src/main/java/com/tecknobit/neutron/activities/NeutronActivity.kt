package com.tecknobit.neutron.activities

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

abstract class NeutronActivity : ComponentActivity() {

    @Composable
    protected fun DisplayContent(
        cardContent: @Composable ColumnScope.() -> Unit,
        uiContent: @Composable ColumnScope.() -> Unit
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
                ),
                content = {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                all = 16.dp
                            ),
                        content = cardContent
                    )
                }
            )
            uiContent()
        }
    }

}
