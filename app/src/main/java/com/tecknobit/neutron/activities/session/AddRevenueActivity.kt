package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.session.MainActivity.Companion.currency
import com.tecknobit.neutron.ui.InsertionLabelBadge
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import java.util.Calendar

class AddRevenueActivity : ComponentActivity() {

    private lateinit var showKeyboard: MutableState<Boolean>

    private lateinit var revenueValue: MutableState<String>

    private val digits : ArrayDeque<Int> = ArrayDeque()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            showKeyboard = remember { mutableStateOf(true) }
            revenueValue = remember { mutableStateOf("0") }
            NeutronTheme {
                Scaffold (
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
                                .height(350.dp)
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
                                text = "${revenueValue.value}$currency",
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
                                    top = 275.dp
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
    }

    @Composable
    private fun Keyboard() {
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
                            ActionButton(
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp
                                    ),
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
                            NumberKeyboardButton(
                                modifier = Modifier
                                    .padding(
                                        start = 40.dp
                                    )
                                    .weight(1f),
                                number = 0
                            )
                            KeyboardButton(
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp
                                    )
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

    @Wrapper
    @Composable
    private fun NumberKeyboardButton(
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

    @Composable
    private fun KeyboardButton(
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

    @Composable
    private fun ActionButton(
        modifier: Modifier,
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val revenueTitle = remember { mutableStateOf("") }
            val revenueDescription = remember { mutableStateOf("") }
            val calendar = Calendar.getInstance()
            val formatter = TimeFormatter.getInstance()
            var displayDatePickerDialog by remember { mutableStateOf(false) }
            val dateFormat = "dd/MM/yyyy"
            var currentDate by remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis()
            )
            val timeFormat = "HH:mm:ss"
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            var currentTime by remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val timePickerState = rememberTimePickerState(
                initialHour = if(is24HourFormat(LocalContext.current))
                    calendar.get(Calendar.HOUR_OF_DAY)
                else
                    calendar.get(Calendar.HOUR),
                initialMinute = calendar.get(Calendar.MINUTE)
            )
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        top = 25.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 25.dp,
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                var isProjectRevenue by remember { mutableStateOf(false) }
                SingleChoiceSegmentedButtonRow (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { showKeyboard.value = !showKeyboard.value}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    SegmentedButton(
                        selected = !isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.general_revenue)
                        )
                    }
                    SegmentedButton(
                        selected = isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.project)
                        )
                    }
                }
                NeutronOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = revenueTitle,
                    label = R.string.title
                )
                if(!isProjectRevenue) {
                    NeutronOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 250.dp
                            ),
                        value = revenueDescription,
                        label = R.string.description,
                        isTextArea = true
                    )
                    Labels()
                }
                TimeInfo(
                    info = R.string.insertion_date,
                    infoValue = currentDate,
                    onClick = { displayDatePickerDialog = true }
                )
                if(displayDatePickerDialog) {
                    DatePickerDialog(
                        onDismissRequest = { displayDatePickerDialog = false },
                        dismissButton = {
                            TextButton(
                                onClick = { displayDatePickerDialog = false }
                            ) {
                                Text(
                                    text = getString(R.string.dismiss)
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    currentDate = formatter.formatAsString(
                                        dateState.selectedDateMillis!!,
                                        dateFormat
                                    )
                                    displayDatePickerDialog = false
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
                    info = R.string.insertion_time,
                    infoValue = currentTime,
                    onClick = { displayTimePickerDialog.value = true }
                )
                TimePickerDialog(
                    showTimePicker = displayTimePickerDialog,
                    timeState = timePickerState,
                    confirmAction = {
                        currentTime = "${timePickerState.hour}:${timePickerState.minute}:00"
                        displayTimePickerDialog.value = false
                    }
                )
                NeutronButton(
                    onClick = {
                        // TODO: MAKE THE REQUEST THEN
                        navBack()
                    },
                    text = if(isProjectRevenue)
                        R.string.add_project
                    else
                        R.string.add_revenue
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Labels() {
        val labels = remember { mutableStateListOf<RevenueLabel>(
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
            ),
            RevenueLabel(
                "ffAGAGA",
                "Proj",
                ProjectRevenue.PROJECT_LABEL_COLOR
            )
        ) }
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.labels),
                fontSize = 18.sp
            )
            LazyRow (
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = 5.dp,
                    bottom = 5.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if(labels.size < 5) {
                    stickyHeader {
                        FloatingActionButton(
                            modifier = Modifier
                                .size(35.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(
                                size = 10.dp
                            ),
                            onClick = {
                                // TODO: DISPLAY THE DIALOG TO ADD THE LABELS
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
                items(
                    items = labels,
                    key = { it.text }
                ) { label ->
                    InsertionLabelBadge(
                        labels = labels,
                        label = label
                    )
                }
            }
            HorizontalDivider()
        }
    }

    @Composable
    private fun TimeInfo(
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimePickerDialog(
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

    private fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}
