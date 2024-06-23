package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.neutron.activities.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutroncore.records.User.CURRENCY_KEY
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.Revenue.returnRevenues
import java.time.YearMonth

class MainActivityViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val monthFormatter = TimeFormatter.getInstance("MM")

    private val _revenues = MutableLiveData<MutableList<Revenue>>(mutableListOf())
    val revenues: LiveData<MutableList<Revenue>> = _revenues

    private val _walletBalance = MutableLiveData(0.0)
    val walletBalance: LiveData<Double> = _walletBalance

    private val _walletTrend = MutableLiveData("0")
    val walletTrend: LiveData<String> = _walletTrend

    fun getRevenuesList() {
        if(workInLocal()) {
            // TODO: FETCH FROM LOCAL 
        } else {
            execRefreshingRoutine(
                currentContext = MainActivity::class.java,
                routine = {
                    requester.sendRequest(
                        request = {
                            requester.listRevenues()
                        },
                        onSuccess = { helper ->
                            _revenues.postValue(returnRevenues(helper.getJSONArray(REVENUES_KEY)))
                            localUser.currency =
                                NeutronCurrency.valueOf(helper.getString(CURRENCY_KEY))
                            localUser.profilePic = helper.getString(PROFILE_PIC_KEY)
                            getWalletBalance()
                            getWalletTrend()
                        },
                        onFailure = { showSnack(it) }
                    )
                }
            )
        }
    }

    private fun getWalletBalance() {
        var balance = 0.0
        _revenues.value!!.forEach { revenue ->
            balance += revenue.value
        }
        _walletBalance.postValue(balance)
    }

    private fun getWalletTrend() {
        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.minusMonths(1)
        val currentMonthTrend = getRevenuesPerMonth(
            month = currentMonth
        )
        val lastMonthTrend = getRevenuesPerMonth(
            month = lastMonth
        )
        if (lastMonthTrend == 0.0 && _walletBalance.value!! > 0)
            _walletTrend.postValue(textualizeAssetPercent(100.0))
        else
            _walletTrend.postValue(textualizeAssetPercent(lastMonthTrend, currentMonthTrend, 2))
    }

    private fun getRevenuesPerMonth(
        month: YearMonth
    ): Double {
        val targetMonth = month.monthValue
        var amount = 0.0
        _revenues.value!!.forEach { revenue ->
            if (revenue is ProjectRevenue) {
                val initialRevenue = revenue.initialRevenue
                if (targetMonth == getRevenueMonth(initialRevenue.revenueTimestamp))
                    amount += initialRevenue.value
                revenue.tickets.forEach { ticket ->
                    if (targetMonth == getRevenueMonth(ticket.revenueTimestamp) ||
                        targetMonth == getRevenueMonth(ticket.closingTimestamp)
                    ) {
                        amount += ticket.value
                    }
                }
            } else if (targetMonth == getRevenueMonth(revenue.revenueTimestamp))
                amount += revenue.value
        }
        return amount
    }

    private fun getRevenueMonth(
        timestamp: Long
    ): Int {
        return monthFormatter.formatAsString(timestamp).toInt()
    }

}