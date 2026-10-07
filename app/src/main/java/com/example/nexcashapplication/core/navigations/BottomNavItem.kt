package com.example.nexcashapplication.core.navigations

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.R

data class BottomNavItem(
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Route,
)

val TOP_LEVEL_DESTINATION = listOf(
    BottomNavItem(
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = Route.HomeRoute.Main,
    ),
    BottomNavItem(
        titleRes = R.string.nav_transaction,
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt,
        route = Route.TransactionRoute.Main,

        ),
    BottomNavItem(
        titleRes = R.string.nav_asset,
        selectedIcon = Icons.Filled.AccountBalance,
        unselectedIcon = Icons.Outlined.AccountBalance,
        route = Route.AccountRoute.Main,

        ),
    BottomNavItem(
        titleRes = R.string.nav_insight,
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights,
        route = Route.StatisticRoute.Main,

        ),
    BottomNavItem(
        titleRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        route = Route.SettingsRoute.Main,

        ),
)