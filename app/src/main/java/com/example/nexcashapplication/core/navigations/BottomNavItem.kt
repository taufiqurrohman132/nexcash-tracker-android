package com.example.nexcashapplication.core.navigations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon: ImageVector, val title: String
)

val TOP_LEVEL_DESTINATION = mapOf(
    Route.TodoList to BottomNavItem(
        icon = Icons.Outlined.Check, title = "todos"
    ),
    Route.TodoFavorites to BottomNavItem(
        icon = Icons.Outlined.Favorite, title = "favorit"
    ),
    Route.Settings to BottomNavItem(
        icon = Icons.Outlined.Settings, title = "settings"
    ),
    )
