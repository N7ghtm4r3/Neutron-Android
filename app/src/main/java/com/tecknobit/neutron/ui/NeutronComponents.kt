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
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutron.ui.theme.errorContainerDark
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.InitialRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

lateinit var PROJECT_LABEL: RevenueLabel

@Composable
fun DisplayRevenues(
    revenues: SnapshotStateList<Revenue>,
    navToProject: (Revenue) -> Unit
) {
    if(revenues.isNotEmpty()) {
        val onDelete: (Revenue) -> Unit = { revenue ->
            // TODO: MAKE REQUEST THEN
            revenues.remove(revenue)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralRevenue(
    revenue: Revenue
) {
    var descriptionDisplayed by remember { mutableStateOf(false) }
    val isInitialRevenue = revenue.title == InitialRevenue.INITIAL_REVENUE_KEY
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
                        LazyRow (
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
                                val badge = @Composable {
                                    LabelBadge(
                                        label = label
                                    )
                                }
                                val coroutine = rememberCoroutineScope()
                                if(revenue is TicketRevenue) {
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
                                                text = if(revenue.isClosed)
                                                    "Closed"
                                                else
                                                    "Pending"
                                            )
                                        },
                                        state = state
                                    ) {
                                        badge.invoke()
                                    }
                                } else
                                    badge.invoke()
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
                text = "${revenue.value}${user.currency.symbol}",
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

@Composable
fun NeutronTextField(
    modifier: Modifier = Modifier,
    width: Dp = 280.dp,
    value: MutableState<String>,
    isTextArea: Boolean = false,
    onValueChange: (String) -> Unit = { value.value = it },
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
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun NeutronOutlinedTextField(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    value: MutableState<String>,
    isTextArea: Boolean = false,
    onValueChange: (String) -> Unit = { value.value = it },
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
        keyboardOptions = keyboardOptions
    )
}

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