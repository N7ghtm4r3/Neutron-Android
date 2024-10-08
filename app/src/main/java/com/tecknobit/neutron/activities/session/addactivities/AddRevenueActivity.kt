package com.tecknobit.neutron.activities.session.addactivities

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tecknobit.apimanager.annotations.Structure
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import java.util.Calendar.HOUR
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

/**
 * The **AddRevenueActivity** class is the activity where the user can create and insert a new revenue
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ComponentActivity
 */
@Structure
abstract class AddRevenueActivity: ComponentActivity() {

    /**
     * *calendar* -> the helper used to validate the date values inserted
     */
    private val calendar: Calendar = Calendar.getInstance()

    /**
     * *formatter* -> the helper used to format the time values
     */
    protected val formatter: TimeFormatter = TimeFormatter.getInstance()

    /**
     * *dateFormat* -> the pattern to use to format the date
     */
    protected val dateFormat = "dd/MM/yyyy"

    /**
     * *timeFormat* -> the pattern to use to format the time
     */
    protected val timeFormat = "HH:mm:ss"

    /**
     * *showKeyboard* -> whether display the keyboard
     */
    protected lateinit var showKeyboard: MutableState<Boolean>

    /**
     * *revenueValue* -> the value of the revenue to add
     */
    private lateinit var revenueValue: MutableState<String>

    /**
     * *digits* -> the queue of the current decimal digits inserted in the [revenueValue]
     */
    private val digits : ArrayDeque<Int> = ArrayDeque()

    /**
     * *reviewManager* -> the manager used to allow the user to review the application in app
     */
    private lateinit var reviewManager: ReviewManager

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    /**
     * Function to display the section where the user can insert the revenue data
     *
     * @param revenueValue: the value of the revenue to add
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun AddRevenueUI(
        revenueValue: MutableState<String>
    ) {
        this.revenueValue = revenueValue
        showKeyboard = remember { mutableStateOf(true) }
        reviewManager = ReviewManagerFactory.create(LocalContext.current)
        NeutronTheme {
            Scaffold (
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = { navBack() }
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
                            .height(300.dp)
                            .background(MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = 32.dp,
                                    end = 32.dp
                                ),
                            text = "${revenueValue.value}${localUser.currency.symbol}",
                            color = Color.White,
                            fontFamily = displayFontFamily,
                            fontSize = 50.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Card (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 200.dp
                            ),
                        shape = RoundedCornerShape(
                            topStart = 50.dp,
                            topEnd = 50.dp
                        )
                    ) {
                        Keyboard()
                        InputForm()
                    }
                }
            }
        }
    }

    /**
     * Function to display a custom digits keyboard on screen
     *
     * No-any params required
     */
    @Composable
    protected fun Keyboard() {
        AnimatedVisibility(
            visible = showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                LazyColumn {
                    repeat(3) { j ->
                        item {
                            Row (
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth(),
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                ActionButton(
                                    action = {
                                        if(digits.isNotEmpty()) {
                                            if(revenueValue.value.last() == '.')
                                                revenueValue.value = revenueValue.value.removeSuffix(".")
                                            else {
                                                val digit = digits.removeLast()
                                                revenueValue.value = if(revenueValue.value.contains("."))
                                                    revenueValue.value.removeSuffix(digit.toString())
                                                else
                                                    ((revenueValue.value.toInt() - digit) / 10).toString()
                                            }
                                        } else if(revenueValue.value.last() == '.')
                                            revenueValue.value = revenueValue.value.removeSuffix(".")
                                    },
                                    icon = Icons.AutoMirrored.Filled.Backspace
                                )
                            }
                            NumberKeyboardButton(
                                modifier = Modifier
                                    .weight(1f),
                                number = 0
                            )
                            KeyboardButton(
                                modifier = Modifier
                                    .weight(1f),
                                onClick = {
                                    if(!revenueValue.value.contains("."))
                                        revenueValue.value += "."
                                },
                                text = ".",
                                fontSize = 50.sp
                            )
                        }
                    }
                    item {
                        NeutronButton(
                            modifier = Modifier
                                .padding(
                                    top = 25.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                            onClick = {
                                if(revenueValue.value != "0")
                                    showKeyboard.value = !showKeyboard.value
                            },
                            text = R.string.next
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to create a button for the custom [Keyboard] to insert a number value
     *
     * @param modifier: the modifier to apply to the button
     * @param number: the value of the number to apply to the button
     */
    @Wrapper
    @Composable
    protected fun NumberKeyboardButton(
        modifier: Modifier,
        number: Int
    ) {
        KeyboardButton(
            modifier = modifier,
            onClick = {
                revenueValue.value = if(revenueValue.value.contains(".")) {
                    if(revenueValue.value.split(".")[1].length < 2)
                        revenueValue.value + number
                    else
                        revenueValue.value
                } else
                    (revenueValue.value.toInt() * 10 + number).toString()
                digits.add(number)
            },
            text = number.toString()
        )
    }

    /**
     * Function to create a button for the custom [Keyboard] to execute any action
     *
     * @param modifier: the modifier to apply to the button
     * @param onClick: the action to execute when the button has been clicked
     * @param text: the text of the button
     * @param fontSize: the font size of the [text]
     */
    @Composable
    protected fun KeyboardButton(
        modifier: Modifier,
        onClick: () -> Unit,
        text: String,
        fontSize: TextUnit = 45.sp
    ) {
        TextButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(
                text = text,
                fontSize = fontSize
            )
        }
    }

    /**
     * Function to create a button for the custom [Keyboard] to execute an action
     *
     * @param modifier: the modifier to apply to the button
     * @param action: the action to execute when the button has been clicked
     * @param icon: the icon of the button
     */
    @Composable
    protected fun ActionButton(
        modifier: Modifier = Modifier,
        action: () -> Unit,
        icon: ImageVector
    ) {
        Button(
            modifier = modifier
                .size(75.dp)
                .clip(CircleShape),
            onClick = action,
        ) {
            Icon(
                modifier = Modifier
                    .size(25.dp),
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    /**
     * Function to display the form where the user can insert the details of the revenue to add,
     * so will be different if the revenue is a [GeneralRevenue] or it will be a [ProjectRevenue]
     *
     * No-any params required
     */
    @Composable
    protected abstract fun InputForm()

    /**
     * Function to display a temporal value
     *
     * @param info: the info displayed
     * @param infoValue: value of the time info displayed
     * @param onClick: the action to execute when the button has been clicked
     */
    @Composable
    protected fun TimeInfo(
        info: Int,
        infoValue: String,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(info),
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = infoValue,
                    fontSize = 20.sp,
                    fontFamily = displayFontFamily
                )
                Button(
                    modifier = Modifier
                        .height(25.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 10.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.edit),
                        fontSize = 12.sp
                    )
                }
            }
            HorizontalDivider()
        }
    }

    /**
     * Function to display the section of a temporal value
     *
     * @param dateTitle: the title for the date section
     * @param date: the date value
     * @param displayDatePickerDialog: whether display the [DatePickerDialog]
     * @param dateState: the state attached to the [displayDatePickerDialog]
     * @param timeTitle: the title for the time section
     * @param time: the time value
     * @param displayTimePickerDialog: whether display the [TimePickerDialog]
     * @param timePickerState: the state attached to the [displayTimePickerDialog]
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun TimeInfoSection(
        dateTitle: Int,
        date: MutableState<String>,
        displayDatePickerDialog: MutableState<Boolean>,
        dateState: DatePickerState,
        timeTitle: Int,
        time: MutableState<String>,
        displayTimePickerDialog: MutableState<Boolean>,
        timePickerState: TimePickerState
    ) {
        TimeInfo(
            info = dateTitle,
            infoValue = date.value,
            onClick = { displayDatePickerDialog.value = true }
        )
        if(displayDatePickerDialog.value) {
            DatePickerDialog(
                onDismissRequest = { displayDatePickerDialog.value = false },
                dismissButton = {
                    TextButton(
                        onClick = { displayDatePickerDialog.value = false }
                    ) {
                        Text(
                            text = getString(R.string.dismiss)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            date.value = formatter.formatAsString(
                                dateState.selectedDateMillis!!,
                                dateFormat
                            )
                            displayDatePickerDialog.value = false
                        }
                    ) {
                        Text(
                            text = getString(R.string.confirm)
                        )
                    }
                }
            ) {
                DatePicker(
                    state = dateState
                )
            }
        }
        TimeInfo(
            info = timeTitle,
            infoValue = time.value,
            onClick = { displayTimePickerDialog.value = true }
        )
        TimePickerDialog(
            showTimePicker = displayTimePickerDialog,
            timeState = timePickerState,
            confirmAction = {
                time.value = "${timePickerState.hour}:${timePickerState.minute}:00"
                displayTimePickerDialog.value = false
            }
        )
    }

    /**
     * Function to get a [TimePickerState] to use
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun getTimePickerState(): TimePickerState {
        return rememberTimePickerState(
            initialHour = if(DateFormat.is24HourFormat(LocalContext.current))
                calendar.get(HOUR_OF_DAY)
            else
                calendar.get(HOUR),
            initialMinute = calendar.get(MINUTE)
        )
    }

    /**
     * Function to display the dialog to insert the time value
     *
     * @param showTimePicker: whether display the [TimePickerDialog]
     * @param timeState: the state attached to the [TimePickerDialog]
     * @param confirmAction: the action to execute when the user confirmed
     *
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun TimePickerDialog(
        showTimePicker: MutableState<Boolean>,
        timeState: TimePickerState,
        confirmAction: () -> Unit
    ) {
        if (showTimePicker.value) {
            AlertDialog(
                onDismissRequest = { showTimePicker.value = false },
                title = {},
                text = { TimePicker(state = timeState) },
                dismissButton = {
                    TextButton(
                        onClick = { showTimePicker.value = false }
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = confirmAction
                    ) {
                        Text(
                            text = stringResource(R.string.confirm)
                        )
                    }
                }
            )
        }
    }

    /**
     * Function to execute the back navigation from the current activity to the previous activity
     *
     * No-any params required
     */
    protected abstract fun navBack()

    /**
     * Function to launch the review in-app API
     *
     * @param flowAction: the action to execute when the review in-app finished
     */
    protected fun reviewInApp(
        flowAction: () -> Unit
    ) {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val flow = reviewManager.launchReviewFlow(this, task.result)
                flow.addOnCompleteListener {
                    flowAction.invoke()
                }
                flow.addOnCanceledListener {
                    flowAction.invoke()
                }
            } else
                flowAction.invoke()
        }
        request.addOnFailureListener {
            flowAction.invoke()
        }
    }

}