package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.neutron.viewmodels.NeutronViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.RevenueLabel

/**
 * The **AddRevenuesViewModel** class is the support class used by the [AddRevenuesActivity] and
 * to create new revenues, both [GeneralRevenue] and [ProjectRevenue]
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class AddRevenuesViewModel(
    snackbarHostState: SnackbarHostState
) : AddRevenueViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * *currentDate* -> the current date of the revenue
     */
    lateinit var currentDate: MutableState<String>

    /**
     * *currentTime* -> the current time of the revenue
     */
    lateinit var currentTime: MutableState<String>

    /**
     * *labels* -> the labels attached to the revenue
     */
    lateinit var labels: SnapshotStateList<RevenueLabel>

    /**
     * Wrapper function to create a new revenue
     *
     * @param isProject: whether the new revenue is project or simple general revenue
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun createRevenue(
        isProject: Boolean,
        onSuccess: () -> Unit
    ) {
        if (!isRevenueTitleValid(revenueTitle.value))
            revenueTitleError.value = true
        else {
            if (isProject) {
                createProjectRevenue {
                    onSuccess.invoke()
                }
            } else {
                if (!isRevenueDescriptionValid(revenueDescription.value))
                    revenueDescriptionError.value = true
                else {
                    createGeneralRevenue {
                        onSuccess.invoke()
                    }
                }
            }
        }
    }

    /**
     * Function to create a new general revenue
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    private fun createProjectRevenue(
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.createProjectRevenue(
                    title = revenueTitle.value,
                    value = revenueValue.value.toDouble(),
                    revenueDate = completeRevenueDate(
                        date = currentDate,
                        time = currentTime
                    )
                )
            },
            onSuccess = {
                onSuccess.invoke()
            },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to create a new project revenue
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    private fun createGeneralRevenue(
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.createGeneralRevenue(
                    title = revenueTitle.value,
                    description = revenueDescription.value,
                    value = revenueValue.value.toDouble(),
                    revenueDate = completeRevenueDate(
                        date = currentDate,
                        time = currentTime
                    ),
                    labels = labels.toList()
                )
            },
            onSuccess = {
                onSuccess.invoke()
            },
            onFailure = { showSnack(it) }
        )
    }

}