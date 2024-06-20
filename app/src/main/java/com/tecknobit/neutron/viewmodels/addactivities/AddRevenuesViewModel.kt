package com.tecknobit.neutron.viewmodels.addactivities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.RevenueLabel

class AddRevenuesViewModel(
    snackbarHostState: SnackbarHostState
) : AddRevenueViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var currentDate: MutableState<String>

    lateinit var currentTime: MutableState<String>

    lateinit var labels: SnapshotStateList<RevenueLabel>

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

    private fun createProjectRevenue(
        onSuccess: () -> Unit
    ) {
        if (workInLocal()) {
            // TODO: ADD IN LOCAL DATABASE
        } else {
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
    }

    private fun createGeneralRevenue(
        onSuccess: () -> Unit
    ) {
        if (workInLocal()) {
            // TODO: ADD IN LOCAL DATABASE
        } else {
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

}