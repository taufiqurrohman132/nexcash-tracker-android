package com.example.nexcashapplication.core.navigations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object TodoList: Route

    @Serializable
    data object TodoFavorites: Route

    @Serializable
    data class TodoDetail(val todo: String): Route

    @Serializable
    data object Settings: Route

}