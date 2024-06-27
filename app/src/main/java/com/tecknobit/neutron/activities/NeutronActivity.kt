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
import com.tecknobit.apimanager.annotations.Structure

/**
 * The **NeutronActivity** class is useful to create an activity with the behavior to show the UI
 * data correctly
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ComponentActivity
 */
@Structure
abstract class NeutronActivity : ComponentActivity() {

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    /**
     * Function to display the content of an activity
     *
     * @param modifier: custom modifier to apply to the container [Column]
     * @param contentPadding: the padding to apply to the content
     * @param cardHeight: height of the container [Card]
     * @param cardContent: the content of the [Card] used as top bar
     * @param uiContent: the content to display in the activity
     */
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
