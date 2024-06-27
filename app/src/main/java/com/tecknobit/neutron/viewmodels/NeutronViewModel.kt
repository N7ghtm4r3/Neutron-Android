package com.tecknobit.neutron.viewmodels

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.FetcherManager
import com.tecknobit.equinox.FetcherManager.Companion.activeContext
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester
import com.tecknobit.neutron.helpers.AndroidNeutronRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The **NeutronViewModel** class is the support class used by the related activities to communicate
 * with the backend and to execute the refreshing routines to update the UI data
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
open class NeutronViewModel(
    protected val snackbarHostState: SnackbarHostState
) : ViewModel(), FetcherManagerWrapper {

    companion object {

        /**
         * **requester** -> the instance to manage the requests with the backend
         */
        lateinit var requester: AndroidNeutronRequester

    }

    /**
     * **refreshRoutine** -> the coroutine used to execute the refresh routines
     */
    private val refreshRoutine = CoroutineScope(Dispatchers.Default)

    /**
     * **fetcherManager** -> the manager used to fetch the data from the backend
     */
    private val fetcherManager = FetcherManager(refreshRoutine)

    /**
     * Function to get whether the [refreshRoutine] can start, so if there aren't other jobs that
     * routine is already executing
     *
     * No-any params required
     *
     * @return whether the [refreshRoutine] can start as [Boolean]
     */
    override fun canRefresherStart(): Boolean {
        return fetcherManager.canStart()
    }

    /**
     * Function to suspend the current [refreshRoutine] to execute other requests to the backend,
     * the [isRefreshing] instance will be set as **false** to allow the restart of the routine after executing
     * the other requests
     *
     * No-any params required
     */
    override fun continueToFetch(
        currentContext: Class<*>
    ): Boolean {
        return fetcherManager.continueToFetch(currentContext)
    }

    /**
     * Function to execute the refresh routine designed
     *
     * @param currentContext: the current context where the [refreshRoutine] is executing
     * @param routine: the refresh routine to execute
     * @param repeatRoutine: whether repeat the routine or execute a single time
     * @param refreshDelay: the delay between the execution of the requests
     */
    override fun execRefreshingRoutine(
        currentContext: Class<*>,
        routine: () -> Unit,
        repeatRoutine: Boolean,
        refreshDelay: Long
    ) {
        fetcherManager.execute(
            currentContext = currentContext,
            routine = routine,
            repeatRoutine = repeatRoutine,
            refreshDelay = refreshDelay
        )
    }

    /**
     * Function to restart the current [refreshRoutine] after other requests has been executed,
     * the [isRefreshing] instance will be set as **true** to deny the restart of the routine after executing
     * the other requests
     *
     * No-any params required
     */
    override fun restartRefresher() {
        fetcherManager.restart()
    }

    /**
     * Function to suspend the current [refreshRoutine] to execute other requests to the backend,
     * the [isRefreshing] instance will be set as **false** to allow the restart of the routine after executing
     * the other requests
     *
     * No-any params required
     */
    override fun suspendRefresher() {
        fetcherManager.suspend()
    }

    /**
     * Function to display a response message with a snackbar
     *
     * @param helper: the response message received by the backend
     */
    protected fun showSnack(
        helper: JsonHelper
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            snackbarHostState.showSnackbar(helper.getString(Requester.RESPONSE_MESSAGE_KEY))
        }
    }

    /**
     * Function to set the current active context where the [refreshRoutine] is executing
     *
     * @param context: the current active context
     */
    fun setActiveContext(
        context: Context
    ) {
        activeContext = context::class.java
    }

}