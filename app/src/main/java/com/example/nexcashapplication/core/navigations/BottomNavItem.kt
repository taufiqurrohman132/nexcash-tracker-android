package com.example.nexcashapplication.core.navigations

import com.example.nexcashapplication.R

data class BottomNavItem(
    val iconRes: Int, val title: String
)

val TOP_LEVEL_DESTINATION = mapOf(
    Route.HomeRoute.Main to BottomNavItem(
        iconRes = R.drawable.home, title = "Home"
    ),
    Route.TransactionRoute.Main to BottomNavItem(
        iconRes = R.drawable.transaction, title = "Transaction"
    ),
    Route.AssetRoute.Main to BottomNavItem(
        iconRes = R.drawable.account_balance_bank, title = "Asset"
    ),
    Route.InsightRoute.Main to BottomNavItem(
        iconRes = R.drawable.insights, title = "Insight"
    ),
    Route.SettingsRoute.Main to BottomNavItem(
        iconRes = R.drawable.settings, title = "Settings"
    ),

)
