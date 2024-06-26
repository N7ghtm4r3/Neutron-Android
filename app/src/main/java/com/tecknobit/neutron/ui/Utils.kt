package com.tecknobit.neutron.ui

import androidx.compose.ui.graphics.Color
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue

/**
 * Function to get the color from its hex code
 *
 * @return color as [Color]
 */
fun String.backgroundColor(): Color {
    return Color(("ff" + removePrefix("#").lowercase()).toLong(16))
}

/**
 * Function to get a revenue from list of revenues
 *
 * @param revenueId: the identifier of the revenue to get
 *
 * @return revenue as [ProjectRevenue] if exists, or null
 */
fun List<Revenue>.getProjectRevenue(
    revenueId: String
): ProjectRevenue? {
    forEach { revenue ->
        if(revenue.id == revenueId)
            return revenue as ProjectRevenue
    }
    return null
}