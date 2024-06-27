package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.neutron.viewmodels.NeutronViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue

/**
 * The **AddRevenueViewModel** class is the support class used by the [AddTicketActivity] to create
 * new tickets
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class AddTicketViewModel(
    snackbarHostState: SnackbarHostState
) : AddRevenueViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * *currentOpeningDate* -> the current opening date of the ticket
     */
    lateinit var currentOpeningDate: MutableState<String>

    /**
     * *currentClosingDate* -> the current closing date of the ticket
     */
    lateinit var currentClosingDate: MutableState<String>

    /**
     * *currentOpeningTime* -> the current opening time of the ticket
     */
    lateinit var currentOpeningTime: MutableState<String>

    /**
     * *currentClosingTime* -> the current closing time of the ticket
     */
    lateinit var currentClosingTime: MutableState<String>

    /**
     * Function to execute the request to add a new ticket to the [projectRevenue]
     *
     * @param projectRevenue: the project where attach the ticket
     * @param isClosed: whether the ticket is already closed
     * @param onSuccess: the action to execute if the request has been successful
     */
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