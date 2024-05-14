package com.tecknobit.neutron.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue

fun String.backgroundColor(): Color {
    return Color(("ff" + removePrefix("#").lowercase()).toLong(16))
}

fun SnapshotStateList<Revenue>.getWalletBalance(): Double {
    var balance = 0.0
    forEach { revenue ->
        balance += revenue.value
    }
    return balance
}

