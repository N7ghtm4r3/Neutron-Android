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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

abstract class NeutronActivity : ComponentActivity() {

    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    @Composable
    protected fun DisplayContent(
        modifier: Modifier = Modifier,
        contentPadding: Dp = 16.dp,
        cardHeight: Dp = 140.dp,
        cardContent: @Composable ColumnScope.() -> Unit,
        uiContent: @Composable ColumnScope.() -> Unit
    ) {
        Column (
            modifier = modifier
                .fillMaxSize()
        ) {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
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
                                all = contentPadding
                            ),
                        content = cardContent
                    )
                }
            )
            uiContent()
        }
    }

}
