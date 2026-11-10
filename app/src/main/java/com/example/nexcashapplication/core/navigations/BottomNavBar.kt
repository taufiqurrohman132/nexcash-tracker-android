package com.example.nexcashapplication.core.navigations

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.BackgroundLight
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navController: NavController,
) {

    BottomAppBar(
        modifier = modifier.shadow(
            elevation = 16.dp,
            ambientColor = PrimaryCyan,
            spotColor = PrimaryCyan,
            shape = RectangleShape, // bottom nav bar bentuknya kotak
        ),
        containerColor = BackgroundLight,
        contentColor = Color.Gray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        TOP_LEVEL_DESTINATION.forEach { item ->
            val selectedRoute = currentDestination
                    ?.hierarchy
                    ?.any { it.hasRoute(item.route::class) } == true

            val title = stringResource(item.titleRes)

            NavigationBarItem(
                selected = selectedRoute,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
//                    selectedNavigationIndex = index
//                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (selectedRoute)
                            item.selectedIcon else item.unselectedIcon,
                        contentDescription = title,
                    )
                },
                label = {
                    Text(title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryCyan,    // icon aktif
                    selectedTextColor = PrimaryCyan,    // label aktif
                    unselectedIconColor = Color.Gray,   // icon tidak aktif
                    unselectedTextColor = Color.Gray,   // label tidak aktif
                    indicatorColor = PrimaryCyan.copy(alpha = 0.1f), // bg bulat saat selected
                ),
            )

        }
    }

}