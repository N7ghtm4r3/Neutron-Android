package com.tecknobit.neutron.activities.session.addactivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutron.activities.session.ProjectRevenueActivity
import com.tecknobit.neutron.ui.ErrorUI
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronTextField
import com.tecknobit.neutron.ui.getProjectRevenue
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

class AddTicketActivity : AddRevenueActivity() {

    private var currentProjectRevenue: ProjectRevenue? = null

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeutronTheme {
                currentProjectRevenue = MainActivity.revenues.getProjectRevenue(
                    intent.getStringExtra(IDENTIFIER_KEY)!!
                )
                if(currentProjectRevenue != null)
                    AddRevenueUI()
                else
                    ErrorUI()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            revenueTitle = remember { mutableStateOf("") }
            revenueDescription = remember { mutableStateOf("") }
            var isClosed by remember { mutableStateOf(false) }
            val currentOpeningDate = remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            val currentClosingDate = remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            val displayDatePickerDialog = remember { mutableStateOf(false) }
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = if(isClosed) {
                    object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis <= formatter.formatAsTimestamp(
                                currentClosingDate.value,
                                dateFormat
                            )
                        }
                    }
                } else
                    DatePickerDefaults.AllDates
            )
            val currentOpeningTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            val timePickerState = getTimePickerState()
            val displayClosingDatePickerDialog = remember { mutableStateOf(false) }
            val dateClosingState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= formatter.formatAsTimestamp(
                            currentOpeningDate.value,
                            dateFormat
                        )
                    }
                }
            )
            val currentClosingTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayClosingTimePickerDialog = remember { mutableStateOf(false) }
            val timeClosingPickerState = getTimePickerState()
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Switch(
                        checked = isClosed,
                        onCheckedChange = { isClosed = it }
                    )
                    Text(
                        text = stringResource(R.string.closed)
                    )
                }
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = revenueTitle,
                    label = R.string.title
                )
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        ),
                    value = revenueDescription,
                    label = R.string.description,
                    isTextArea = true
                )
                TimeInfoSection(
                    dateTitle = R.string.opening_date_title,
                    date = currentOpeningDate,
                    displayDatePickerDialog = displayDatePickerDialog,
                    dateState = dateState,
                    timeTitle = R.string.opening_time,
                    time = currentOpeningTime,
                    displayTimePickerDialog = displayTimePickerDialog,
                    timePickerState = timeClosingPickerState
                )
                if(isClosed) {
                    TimeInfoSection(
                        dateTitle = R.string.closing_date_title,
                        date = currentClosingDate,
                        displayDatePickerDialog = displayClosingDatePickerDialog,
                        dateState = dateClosingState,
                        timeTitle = R.string.closing_time,
                        time = currentClosingTime,
                        displayTimePickerDialog = displayClosingTimePickerDialog,
                        timePickerState = timePickerState
                    )
                }
                NeutronButton(
                    onClick = {
                        // TODO: MAKE THE REQUEST THEN
                        navBack()
                    },
                    text = R.string.add_ticket
                )
            }
        }
    }

    override fun navBack() {
        reviewInApp(
            flowAction = {
                val intent = Intent(this, ProjectRevenueActivity::class.java)
                if(currentProjectRevenue != null)
                    intent.putExtra(IDENTIFIER_KEY, currentProjectRevenue!!.id)
                startActivity(intent)
            }
        )
    }

}
