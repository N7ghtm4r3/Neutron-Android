package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.neutron.viewmodels.NeutronViewModel

open class AddRevenueViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val timeFormatter = TimeFormatter.getInstance()

    lateinit var revenueValue: MutableState<String>

    lateinit var revenueTitle: MutableState<String>

    lateinit var revenueTitleError: MutableState<Boolean>

    lateinit var revenueDescription: MutableState<String>

    lateinit var revenueDescriptionError: MutableState<Boolean>

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