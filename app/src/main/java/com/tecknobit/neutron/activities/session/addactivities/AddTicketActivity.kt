package com.tecknobit.neutron.activities.session.addactivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import com.tecknobit.neutron.activities.session.MainActivity.Companion.revenues
import com.tecknobit.neutron.activities.session.ProjectRevenueActivity
import com.tecknobit.neutron.ui.ErrorUI
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronTextField
import com.tecknobit.neutron.ui.getProjectRevenue
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.viewmodels.addactivities.AddTicketViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

/**
 * The **AddTicketActivity** class is the activity where the user can create and insert a new ticket
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see AddRevenueActivity
 * @see ComponentActivity
 */
class AddTicketActivity : AddRevenueActivity() {

    /**
     * *currentProjectRevenue* -> the current project revenue displayed and where the ticket will
     * be attached
     */
    private var currentProjectRevenue: ProjectRevenue? = null

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = AddTicketViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * On create method
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * If your ComponentActivity is annotated with {@link ContentView}, this will
     * call {@link #setContentView(int)} for you.
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeutronTheme {
                currentProjectRevenue = revenues.value!!.getProjectRevenue(
                    intent.getStringExtra(IDENTIFIER_KEY)!!
                )
                if (currentProjectRevenue != null) {
                    viewModel.revenueValue = remember { mutableStateOf("0") }
                    AddRevenueUI(
                        revenueValue = viewModel.revenueValue
                    )
                } else
                    ErrorUI()
            }
        }
    }

    /**
     * Function to display the form where the user can insert the details of the ticket to add
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            viewModel.revenueTitle = remember { mutableStateOf("") }
            viewModel.revenueTitleError = remember { mutableStateOf(false) }
            viewModel.revenueDescription = remember { mutableStateOf("") }
            viewModel.revenueDescriptionError = remember { mutableStateOf(false) }
            var isClosed by remember { mutableStateOf(false) }
            viewModel.currentOpeningDate =
                remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            viewModel.currentClosingDate =
                remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            viewModel.currentOpeningTime =
                remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            viewModel.currentClosingTime =
                remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayDatePickerDialog = remember { mutableStateOf(false) }
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = if(isClosed) {
                    object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis <= formatter.formatAsTimestamp(
                                viewModel.currentClosingDate.value,
                                dateFormat
                            )
                        }
                    }
                } else
                    DatePickerDefaults.AllDates
            )
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            val timePickerState = getTimePickerState()
            val displayClosingDatePickerDialog = remember { mutableStateOf(false) }
            val dateClosingState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= formatter.formatAsTimestamp(
                            viewModel.currentOpeningDate.value,
                            dateFormat
                        )
                    }
                }
            )
            val displayClosingTimePickerDialog = remember { mutableStateOf(false) }
            val timeClosingPickerState = getTimePickerState()
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        top = 20.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 20.dp,
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
                    value = viewModel.revenueTitle,
                    label = R.string.title,
                    errorText = R.string.title_not_valid,
                    isError = viewModel.revenueTitleError,
                    validator = { isRevenueTitleValid(it) }
                )
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        ),
                    value = viewModel.revenueDescription,
                    label = R.string.description,
                    isTextArea = true,
                    errorText = R.string.description_not_valid,
                    isError = viewModel.revenueDescriptionError,
                    validator = { isRevenueDescriptionValid(it) }
                )
                TimeInfoSection(
                    dateTitle = R.string.opening_date_title,
                    date = viewModel.currentOpeningDate,
                    displayDatePickerDialog = displayDatePickerDialog,
                    dateState = dateState,
                    timeTitle = R.string.opening_time,
                    time = viewModel.currentOpeningTime,
                    displayTimePickerDialog = displayTimePickerDialog,
                    timePickerState = timeClosingPickerState
                )
                if(isClosed) {
                    TimeInfoSection(
                        dateTitle = R.string.closing_date_title,
                        date = viewModel.currentClosingDate,
                        displayDatePickerDialog = displayClosingDatePickerDialog,
                        dateState = dateClosingState,
                        timeTitle = R.string.closing_time,
                        time = viewModel.currentClosingTime,
                        displayTimePickerDialog = displayClosingTimePickerDialog,
                        timePickerState = timePickerState
                    )
                }
                NeutronButton(
                    onClick = {
                        viewModel.addTicket(
                            projectRevenue = currentProjectRevenue!!,
                            isClosed = isClosed,
                            onSuccess = { navBack() }
                        )
                    },
                    text = R.string.add_ticket
                )
            }
        }
    }

    /**
     * Function to execute the back navigation from the current activity to the previous activity, but
     * will be checked if the user can review the app
     *
     * No-any params required
     */
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

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or {@link #onPause}. This
     * is usually a hint for your activity to start interacting with the user, which is a good
     * indicator that the activity became active and ready to receive input. This sometimes could
     * also be a transit state toward another resting state. For instance, an activity may be
     * relaunched to {@link #onPause} due to configuration changes and the activity was visible,
     * but wasn’t the top-most activity of an activity task. {@link #onResume} is guaranteed to be
     * called before {@link #onPause} in this case which honors the activity lifecycle policy and
     * the activity eventually rests in {@link #onPause}.
     *
     * <p>On platform versions prior to {@link android.os.Build.VERSION_CODES#Q} this is also a good
     * place to try to open exclusive-access devices or to get access to singleton resources.
     * Starting  with {@link android.os.Build.VERSION_CODES#Q} there can be multiple resumed
     * activities in the system simultaneously, so {@link #onTopResumedActivityChanged(boolean)}
     * should be used for that purpose instead.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * Will be set the **[FetcherManager.activeContext]** with the current context
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     * @see #onTopResumedActivityChanged(boolean)
     */
    override fun onResume() {
        super.onResume()
        viewModel.setActiveContext(this)
    }

}
