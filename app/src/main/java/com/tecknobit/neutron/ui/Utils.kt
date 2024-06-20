package com.tecknobit.neutron.ui

import androidx.compose.ui.graphics.Color
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue

fun String.backgroundColor(): Color {
    return Color(("ff" + removePrefix("#").lowercase()).toLong(16))
}

fun List<Revenue>.getWalletBalance(): Double {
    var balance = 0.0
    forEach { revenue ->
        balance += revenue.value
    }
    return balance
}

fun List<Revenue>.getProjectRevenue(
    revenueId: String
): ProjectRevenue? {
    forEach { revenue ->
        if(revenue.id == revenueId)
            return revenue as ProjectRevenue
    }
    return null
}