package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutron.activities.session.ProjectRevenueActivity
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue

class ProjectRevenueActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _projectRevenue = MutableLiveData<ProjectRevenue>()
    val projectRevenue: LiveData<ProjectRevenue> = _projectRevenue

    lateinit var showDeleteProject: MutableState<Boolean>

    override fun restartRefresher() {
        getProjectRevenue()
    }

    fun setInitialProjectRevenue(
        projectRevenue: ProjectRevenue
    ) {
        _projectRevenue.value = projectRevenue
    }

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