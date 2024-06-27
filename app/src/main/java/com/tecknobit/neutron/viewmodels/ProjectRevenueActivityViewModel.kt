package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutron.activities.session.ProjectRevenueActivity
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue

/**
 * The **ProjectRevenueActivityViewModel** class is the support class used by the [ProjectRevenueActivity]
 * to refresh and work on a project
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class ProjectRevenueActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **_projectRevenue** -> the current project revenue displayed
     */
    private val _projectRevenue = MutableLiveData<ProjectRevenue>()
    val projectRevenue: LiveData<ProjectRevenue> = _projectRevenue

    /**
     * **showDeleteProject** -> whether show the dialog to warn about the project deletion
     */
    lateinit var showDeleteProject: MutableState<Boolean>

    /**
     * Function to restart the current [refreshRoutine] after other requests has been executed,
     * will relaunched the [getProjectRevenue] routine
     *
     * No-any params required
     */
    override fun restartRefresher() {
        getProjectRevenue()
    }

    /**
     * Function to set the initial value of the [_projectRevenue]
     *
     * @param projectRevenue: the initial project value to set
     */
    fun setInitialProjectRevenue(
        projectRevenue: ProjectRevenue
    ) {
        _projectRevenue.value = projectRevenue
    }

    /**
     * Function to execute the refreshing routine to update the [_projectRevenue]
     *
     * No-any params required
     */
    fun getProjectRevenue() {
        execRefreshingRoutine(
            currentContext = ProjectRevenueActivity::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.getProjectRevenue(
                            revenue = _projectRevenue.value!!
                        )
                    },
                    onSuccess = { helper ->
                        _projectRevenue.postValue(
                            ProjectRevenue(helper.getJSONObject(RESPONSE_MESSAGE_KEY))
                        )
                    },
                    onFailure = { showSnack(it) }
                )
            },
            repeatRoutine = true
        )
    }

    /**
     * Function to execute the request to close a [TicketRevenue] of the project
     *
     * @param ticket: the ticket to close
     */
    fun closeTicket(
        ticket: TicketRevenue
    ) {
        suspendRefresher()
        requester.sendRequest(
            request = {
                requester.closeProjectRevenueTicket(
                    projectRevenue = _projectRevenue.value!!,
                    ticket = ticket
                )
            },
            onSuccess = {
                restartRefresher()
            },
            onFailure = {
                restartRefresher()
                showSnack(it)
            }
        )
    }

    /**
     * Function to execute the request to delete a [TicketRevenue] of the project
     *
     * @param ticket: the ticket to delete
     */
    fun deleteTicket(
        ticket: TicketRevenue
    ) {
        requester.sendRequest(
            request = {
                requester.deleteProjectRevenueTicket(
                    projectRevenue = _projectRevenue.value!!,
                    ticket = ticket
                )
            },
            onSuccess = {},
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to delete the [_projectRevenue] displayed
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun deleteProjectRevenue(
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.deleteRevenue(
                    revenue = _projectRevenue.value!!
                )
            },
            onSuccess = {
                onSuccess.invoke()
            },
            onFailure = {
                showDeleteProject.value = false
                restartRefresher()
                showSnack(it)
            }
        )
    }

}