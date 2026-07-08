package com.example.nexcashapplication.core.navigations

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.nexcashapplication.ui.theme.BackgroundLight
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onSelectKey: (NavKey) -> Unit,
    selectionKey: NavKey
) {
    BottomAppBar(
        modifier = modifier.shadow(
            elevation = 16.dp,
            ambientColor = PrimaryCyan,
            spotColor = PrimaryCyan,
            shape = RectangleShape // bottom nav bar bentuknya kotak
        ),
        containerColor = BackgroundLight,
        contentColor = Color.Gray,
    ) {
        TOP_LEVEL_DESTINATION.forEach { (topLevelDestination, data) ->
            NavigationBarItem(
                selected = topLevelDestination == selectionKey,
                onClick = {
                    onSelectKey(topLevelDestination as NavKey)
                },
                icon = {
                    Icon(painter = painterResource(data.iconRes), contentDescription = data.title)

                },
                label = {
                    Text(data.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryCyan,    // icon aktif
                    selectedTextColor = PrimaryCyan,    // label aktif
                    unselectedIconColor = Color.Gray,   // icon tidak aktif
                    unselectedTextColor = Color.Gray,   // label tidak aktif
                    indicatorColor = PrimaryCyan.copy(alpha = 0.1f) // bg bulat saat selected
                )
            )

        }
    }

}