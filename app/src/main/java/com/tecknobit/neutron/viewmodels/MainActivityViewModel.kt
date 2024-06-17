package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.returnRevenues

class MainActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _revenues = MutableLiveData<List<Revenue>>()
    val revenues: LiveData<List<Revenue>> = _revenues

    fun getRevenuesList() {
        execRefreshingRoutine(
            currentContext = this::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.listRevenues()
                    },
                    onSuccess = { helper ->
                        _revenues.value = returnRevenues(helper.getJSONArray(RESPONSE_MESSAGE_KEY))
                    },
                    onFailure = {

                    }
                )
            },
        )
    }

}