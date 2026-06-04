package com.example.nexcashapplication.core.navigations

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import java.nio.channels.SelectionKey

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onSelectKey: (NavKey) -> Unit,
    selectionKey: NavKey
) {
    BottomAppBar(
        modifier= modifier,

    ){
        TOP_LEVEL_DESTINATION.forEach { (topLevelDestination, data) ->
            NavigationBarItem(
                selected = topLevelDestination == selectionKey,
                onClick = {
                    onSelectKey(topLevelDestination as NavKey)
                },
                icon = {
                    Icon(imageVector = data.icon, contentDescription = data.title)

                },
                label = {
                    Text(data.title)
                }
            )

        }
    }

}