package com.tecknobit.neutron.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SpeakerNotesOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinox.Requester
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.ui.theme.errorContainerDark
import com.tecknobit.neutron.viewmodels.NeutronViewModel.Companion.requester
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * **PROJECT_LABEL** the label used to indicate a [ProjectRevenue]
 */
lateinit var PROJECT_LABEL: RevenueLabel

/**
 * Function to display the user revenues
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 * @param revenues: the list of the revenues to display
 * @param navToProject: the function to navigate to a project screen
 */
@Composable
fun DisplayRevenues(
    snackbarHostState: SnackbarHostState,
    revenues: MutableList<Revenue>,
    navToProject: (Revenue) -> Unit
) {
    if(revenues.isNotEmpty()) {
        val onDelete: (Revenue) -> Unit = { revenue ->
            requester.sendRequest(
                request = {
                    requester.deleteRevenue(
                        revenue = revenue
                    )
                },
                onSuccess = {},
                onFailure = { helper ->
                    CoroutineScope(Dispatchers.IO).launch {
                        snackbarHostState.showSnackbar(helper.getString(Requester.RESPONSE_MESSAGE_KEY))
                    }
                }
            )
        }
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
                        onDelete = onDelete
                    ) {
                        GeneralRevenue(
                            revenue = revenue
                        )
                    }
                } else {
                    SwipeToDeleteContainer(
                        item = revenue,
                        onDelete = onDelete
                    ) {
                        ProjectRevenue(
                            revenue = revenue as ProjectRevenue,
                            navToProject = navToProject
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

/**
 * Function to display a general revenue
 *
 * @param revenue: the revenue to display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralRevenue(
    revenue: Revenue
) {
    var descriptionDisplayed by remember { mutableStateOf(false) }
    val isInitialRevenue = revenue.title == null
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = if(isInitialRevenue)
                        stringResource(R.string.initial_revenue)
                    else
                        revenue.title,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                RevenueInfo(
                    revenue = revenue
                )
            },
            trailingContent = {
                if(!isInitialRevenue) {
                    Column {
                        if (revenue is TicketRevenue) {
                            val coroutine = rememberCoroutineScope()
                            val state = rememberTooltipState()
                            TooltipBox(
                                modifier = Modifier
                                    .clickable {
                                        coroutine.launch {
                                            state.show(MutatePriority.Default)
                                        }
                                    },
                                positionProvider = TooltipDefaults
                                    .rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    Text(
                                        text = if (revenue.isClosed)
                                            "Closed"
                                        else
                                            "Pending"
                                    )
                                },
                                state = state
                            ) {
                                LabelBadge(
                                    label = revenue.currentLabel
                                )
                            }
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .widthIn(
                                        max = 100.dp
                                    ),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(
                                    items = (revenue as GeneralRevenue).labels,
                                    key = {
                                        if (it.id != null)
                                            it.id
                                        else
                                            UUID.randomUUID()
                                    }
                                ) { label ->
                                    LabelBadge(
                                        label = label
                                    )
                                }
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
            }
        )
        AnimatedVisibility(
            visible = descriptionDisplayed
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                text = (revenue as GeneralRevenue).description,
                textAlign = TextAlign.Justify
            )
        }
    }
    if(isInitialRevenue)
        HorizontalDivider()
}

/**
 * Function to display a project revenue
 *
 * @param revenue: the project to display
 * @param navToProject: the function to navigate to a project screen
 */
@Composable
private fun ProjectRevenue(
    revenue: ProjectRevenue,
    navToProject: (Revenue) -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable {
                navToProject.invoke(revenue)
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
                    onClick = { navToProject.invoke(revenue) }
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

/**
 * Function to display the info of a revenue
 *
 * @param revenue: the revenue to display its info
 */
@Composable
fun RevenueInfo(
    revenue: Revenue
) {
    val isTicket = revenue is TicketRevenue
    Column {
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.revenue)
            )
            Text(
                text = "${revenue.value}${localUser.currency.symbol}",
                fontFamily = displayFontFamily
            )
        }
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(
                    if(isTicket)
                        R.string.opening_date
                    else
                        R.string.date
                )
            )
            Text(
                text = revenue.revenueDate,
                fontFamily = displayFontFamily
            )
        }
        if(isTicket) {
            val ticket = revenue as TicketRevenue
            if(ticket.isClosed) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.closing_date)
                    )
                    Text(
                        text = ticket.closingDate,
                        fontFamily = displayFontFamily
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.duration),
                    )
                    Text(
                        text = TimeUnit.MILLISECONDS.toDays(ticket.duration).toString()
                                + " " + stringResource(R.string.days),
                        fontFamily = displayFontFamily
                    )
                }
            }
        }
    }
}

/**
 * Function to display a label as badge
 *
 * @param label: the label to display
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabelBadge(
    label: RevenueLabel
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
                )
                .basicMarquee(),
            text = label.text,
            maxLines = 1
        )
    }
}

/**
 * Function to display the preview of a label when it is inserting
 *
 * @param modifier: the modifier of the label badge
 * @param labels: the current labels list
 * @param label: the label which is creating
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InsertionLabelBadge(
    modifier: Modifier = Modifier,
    labels: SnapshotStateList<RevenueLabel>? = null,
    label: RevenueLabel
) {
    Card (
        modifier = modifier
            .widthIn(
                max = 125.dp
            )
            .height(35.dp),
        colors = CardDefaults.cardColors(
            containerColor = label.color.backgroundColor()
        ),
        shape = RoundedCornerShape(
            size = 5.dp
        )
    ) {
        Row (
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = if (labels == null)
                        10.dp
                    else
                        0.dp
                )
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = label.text,
                maxLines = 1
            )
            if(labels != null) {
                IconButton(
                    onClick = { labels.remove(label) }
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircleOutline,
                        null
                    )
                }
            }
        }
    }
}

/**
 * Function to display a list of tickets
 *
 * @param projectRevenue: the project where the tickets are attached
 * @param onRight: the action to execute when the user swipe to the right
 * @param onDelete: the action to execute when the user want to delete a ticket
 */
@Composable
fun DisplayTickets(
    projectRevenue: ProjectRevenue,
    onRight: (TicketRevenue) -> Unit,
    onDelete: (TicketRevenue) -> Unit
) {
    val tickets = projectRevenue.tickets.toMutableStateList()
    if (tickets.isNotEmpty()) {
        LazyColumn {
            item {
                GeneralRevenue(
                    revenue = projectRevenue.initialRevenue
                )
            }
            items(
                key = { ticket -> ticket.id },
                items = tickets
            ) { ticket ->
                SwipeToDeleteContainer(
                    item = ticket,
                    onRight = if (!ticket.isClosed) {
                        {
                            onRight.invoke(ticket)
                        }
                    } else
                        null,
                    onDelete = {
                        onDelete.invoke(ticket)
                    }
                ) {
                    GeneralRevenue(
                        revenue = ticket
                    )
                }
                HorizontalDivider()
            }
        }
    } else {
        GeneralRevenue(
            revenue = projectRevenue.initialRevenue
        )
        EmptyListUI(
            icon = Icons.AutoMirrored.Filled.StickyNote2,
            subText = R.string.no_tickets_yet
        )
    }
}

/**
 * Function to create the container to manage the swipe gestures
 *
 * @param item: the current item to manage
 * @param onRight: the action to execute when the user swipe to the right
 * @param onDelete: the action to execute when the user want to delete the item
 * @param animationDuration: the duration of the animation
 * @param content: the content to display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onRight: ((T) -> Unit)? = null,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    var swipedToRight by remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                EndToStart -> {
                    isRemoved = true
                    true
                }
                StartToEnd -> {
                    swipedToRight = true
                    true
                }
                else -> false
            }
        }
    )
    if(onRight != null) {
        LaunchedEffect(key1 = swipedToRight) {
            if(swipedToRight) {
                delay(animationDuration.toLong())
                onRight(item)
            }
        }
    }
    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }
    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                SwipeBackground(swipeDismissState = state)
            },
            content = { content(item) },
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = onRight != null
        )
    }
}

/**
 * Function to display the background after the user swiped
 *
 * @param swipeDismissState: the swipe dismiss state used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    swipeDismissState: SwipeToDismissBoxState
) {
    val isEndToStart = swipeDismissState.dismissDirection == EndToStart
    if(isEndToStart || swipeDismissState.dismissDirection == StartToEnd) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isEndToStart)
                        errorContainerDark
                    else
                        TicketRevenue.CLOSED_TICKET_LABEL_COLOR.backgroundColor()
                )
                .padding(16.dp),
            contentAlignment = if(isEndToStart)
                Alignment.CenterEnd
            else
                Alignment.CenterStart
        ) {
            Icon(
                imageVector = if(isEndToStart)
                    Icons.Default.Delete
                else
                    Icons.Default.CheckCircleOutline,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

/**
 * Function to display a custom [AlertDialog]
 *
 * @param show: whether show the alert dialog
 * @param icon: the icon of the alert dialog
 * @param onDismissAction: the action to execute when the alert dialog has been dismissed
 * @param title: the title of the alert dialog
 * @param text: the text displayed in the alert dialog
 * @param dismissAction: the action to execute when the user dismissed the action
 * @param dismissText: the text of the dismiss [TextButton]
 * @param confirmAction: the action to execute when the used confirmed the action
 * @param confirmText: the text of the confirm [TextButton]
 */
@Composable
fun NeutronAlertDialog(
    show: MutableState<Boolean>,
    icon: ImageVector? = null,
    onDismissAction: () -> Unit = { show.value = false },
    title: Int,
    text: Int,
    dismissAction: () -> Unit = onDismissAction,
    dismissText: Int = R.string.dismiss,
    confirmAction: () -> Unit,
    confirmText: Int = R.string.confirm
) {
    NeutronAlertDialog(
        show = show,
        icon = icon,
        onDismissAction = onDismissAction,
        title = title,
        text = {
            Text(
                text = stringResource(id = text),
                textAlign = TextAlign.Justify
            )
        },
        dismissAction = dismissAction,
        dismissText = dismissText,
        confirmAction = confirmAction,
        confirmText = confirmText
    )
}

/**
 * Function to display a custom [AlertDialog]
 *
 * @param show: whether show the alert dialog
 * @param icon: the icon of the alert dialog
 * @param onDismissAction: the action to execute when the alert dialog has been dismissed
 * @param title: the title of the alert dialog
 * @param text: the text displayed in the alert dialog
 * @param dismissAction: the action to execute when the user dismissed the action
 * @param dismissText: the text of the dismiss [TextButton]
 * @param confirmAction: the action to execute when the used confirmed the action
 * @param confirmText: the text of the confirm [TextButton]
 */
@Composable
fun NeutronAlertDialog(
    show: MutableState<Boolean>,
    icon: ImageVector? = null,
    onDismissAction: () -> Unit = { show.value = false },
    title: Int,
    text: @Composable () -> Unit,
    dismissAction: () -> Unit = onDismissAction,
    dismissText: Int = R.string.dismiss,
    confirmAction: () -> Unit,
    confirmText: Int = R.string.confirm
) {
    if(show.value) {
        AlertDialog(
            icon = {
                if(icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            onDismissRequest = onDismissAction,
            title = {
                Text(
                    text = stringResource(id = title)
                )
            },
            text = text,
            dismissButton = {
                TextButton(
                    onClick = dismissAction
                ) {
                    Text(
                        text = stringResource(id = dismissText)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = confirmAction
                ) {
                    Text(
                        text = stringResource(id = confirmText)
                    )
                }
            }
        )
    }
}

/**
 * Function to display a custom [TextField]
 *
 * @param modifier: the modifier of the text field
 * @param width: the width of the text field
 * @param value: the action to execute when the alert dialog has been dismissed
 * @param isTextArea: whether the text field is a text area or simple text field
 * @param validator: the function to invoke to validate the input
 * @param isError: whether the text field is in an error state
 * @param errorText: the error text to display if [isError] is true
 * @param onValueChange the callback that is triggered when the input service updates the text. An
 * updated text comes as a parameter of the callback
 * @param label: the label displayed in the text field
 * @param keyboardOptions software keyboard options that contains configuration
 */
@Composable
fun NeutronTextField(
    modifier: Modifier = Modifier,
    width: Dp = 280.dp,
    value: MutableState<String>,
    isTextArea: Boolean = false,
    validator: ((String) -> Boolean)? = null,
    isError: MutableState<Boolean> = remember { mutableStateOf(false) },
    errorText: Int? = null,
    onValueChange: (String) -> Unit = {
        if (validator != null)
            isError.value = value.value.isNotEmpty() && !validator.invoke(it)
        value.value = it
    },
    label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        modifier = modifier
            .width(width),
        value = value.value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(label)
            )
        },
        singleLine = !isTextArea,
        maxLines = 25,
        keyboardOptions = keyboardOptions,
        isError = isError.value,
        supportingText = if (isError.value && errorText != null) {
            {
                Text(
                    text = stringResource(id = errorText)
                )
            }
        } else
            null
    )
}

/**
 * Function to display a custom [OutlinedTextField]
 *
 * @param modifier: the modifier of the text field
 * @param width: the width of the text field
 * @param value: the action to execute when the alert dialog has been dismissed
 * @param mustBeInLowerCase: whether the input must be in lower case format
 * @param isTextArea: whether the text field is a text area or simple text field
 * @param validator: the function to invoke to validate the input
 * @param isError: whether the text field is in an error state
 * @param errorText: the error text to display if [isError] is true
 * @param onValueChange the callback that is triggered when the input service updates the text. An
 * updated text comes as a parameter of the callback
 * @param label: the label displayed in the text field
 * @param trailingIcon: the optional trailing icon to be displayed at the end of the text field container
 * @param visualTransformation: transforms the visual representation of the input [value]
 * @param keyboardOptions software keyboard options that contains configuration
 */
@Composable
fun NeutronOutlinedTextField(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    value: MutableState<String>,
    mustBeInLowerCase: Boolean = false,
    isTextArea: Boolean = false,
    validator: ((String) -> Boolean)? = null,
    isError: MutableState<Boolean> = remember { mutableStateOf(false) },
    errorText: Int? = null,
    onValueChange: (String) -> Unit = {
        if (validator != null)
            isError.value = value.value.isNotEmpty() && !validator.invoke(it)
        value.value = if (mustBeInLowerCase)
            it.lowercase()
        else
            it
    },
    label: Int,
    trailingIcon:  @Composable (() -> Unit)? = {
        IconButton(
            onClick = { value.value = "" }
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier
            .width(width),
        value = value.value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(label)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = !isTextArea,
        maxLines = 25,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError.value,
        supportingText = if (isError.value && errorText != null) {
            {
                Text(
                    text = stringResource(id = errorText)
                )
            }
        } else
            null
    )
}

/**
 * Function to display a custom [Button]
 *
 * @param modifier: the modifier to apply to the button
 * @param onClick: the action to execute when the button is clicked
 * @param text: the text of the button
 * @param colors: the colors of the button
 */
@Composable
fun NeutronButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: Int,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(
            size = 15.dp
        ),
        colors = colors,
        onClick = onClick
    ) {
        Text(
            text = stringResource(text),
            fontSize = 18.sp
        )
    }
}

/**
 * Function to display a layout when a list of values is empty
 *
 * @param icon: the icon to display
 * @param subText: the description of the layout
 */
@Composable
fun EmptyListUI(
    icon: ImageVector,
    subText: Int
) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(100.dp),
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = stringResource(subText),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Function to display a layout when an error occurred
 *
 * @param retryAction: the retry action to execute
 */
@Composable
fun ErrorUI(
    retryAction: @Composable (() -> Unit)? = null
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(100.dp),
            imageVector = Icons.Default.Error,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = errorContainerDark
            )
        )
        Text(
            text = stringResource(R.string.an_error_occurred),
            color = errorContainerDark
        )
        if(retryAction != null) {
            var retryActionStart by remember { mutableStateOf(false) }
            TextButton(
                onClick = { retryActionStart = true }
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
            if(retryActionStart)
                retryAction.invoke()
        }
    }
}