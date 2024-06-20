package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

class AddTicketViewModel(
    snackbarHostState: SnackbarHostState
) : AddRevenueViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var currentOpeningDate: MutableState<String>

    lateinit var currentClosingDate: MutableState<String>

    lateinit var currentOpeningTime: MutableState<String>

    lateinit var currentClosingTime: MutableState<String>

    fun addTicket(
        projectRevenue: ProjectRevenue,
        isClosed: Boolean,
        onSuccess: () -> Unit
    ) {
        if (!isRevenueTitleValid(revenueTitle.value))
            revenueTitleError.value = true
        else if (!isRevenueDescriptionValid(revenueDescription.value))
            revenueDescriptionError.value = true
        else {
            requester.sendRequest(
                request = {
                    requester.addTicketToProjectRevenue(
                        projectRevenueId = projectRevenue.id,
                        ticketTitle = revenueTitle.value,
                        ticketDescription = revenueDescription.value,
                        ticketValue = revenueValue.value.toDouble(),
                        openingDate = completeRevenueDate(
                            date = currentOpeningDate,
                            time = currentOpeningTime
                        ),
                        closingDate = if (isClosed) {
                            completeRevenueDate(
                                date = currentClosingDate,
                                time = currentClosingTime
                            )
                        } else
                            -1L
                    )
                },
                onSuccess = {
                    onSuccess.invoke()
                },
                onFailure = { showSnack(it) }
            )
        }
    }

}