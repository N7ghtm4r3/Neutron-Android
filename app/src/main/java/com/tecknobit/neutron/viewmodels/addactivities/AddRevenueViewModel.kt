package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.neutron.viewmodels.NeutronViewModel

/**
 * The **AddRevenueViewModel** class is the support class used by the [AddRevenuesActivity] and
 * [AddTicketActivity] to create new revenues
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
open class AddRevenueViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * *timeFormatter* -> the helper used to format the time values
     */
    private val timeFormatter = TimeFormatter.getInstance()

    /**
     * *revenueValue* -> the value of the revenue
     */
    lateinit var revenueValue: MutableState<String>

    /**
     * **revenueTitle** -> the title of the revenue
     */
    lateinit var revenueTitle: MutableState<String>

    /**
     * **revenueTitleError** -> whether the [revenueTitle] field is not valid
     */
    lateinit var revenueTitleError: MutableState<Boolean>

    /**
     * **revenueDescription** -> the description of the revenue
     */
    lateinit var revenueDescription: MutableState<String>

    /**
     * **revenueDescriptionError** -> whether the [revenueDescription] field is not valid
     */
    lateinit var revenueDescriptionError: MutableState<Boolean>

    /**
     * Function to complete the date of the revenue in the complete date format
     *
     * **dd/MM/yyyy HH:mm**
     *
     * @param date: the date value
     * @param time: the time value
     *
     * @return the complete date as [Long]
     */
    protected open fun completeRevenueDate(
        date: MutableState<String>,
        time: MutableState<String>
    ): Long {
        return timeFormatter.formatAsTimestamp(
            date.value,
            "dd/MM/yyyy"
        ) + timeFormatter.formatAsTimestamp(
            time.value,
            "HH:mm:ss"
        )
    }

}