package com.example.nexcashapplication.core.navigations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    sealed interface HomeRoute : Route {
        @Serializable
        data object Main : HomeRoute
    }

    @Serializable
    sealed interface TransactionRoute : Route {
        @Serializable
        data object Main : TransactionRoute

        @Serializable
        data class Detail(val transactionId: String) : TransactionRoute

        @Serializable
        data object AddNew : TransactionRoute
    }

    @Serializable
    sealed interface AssetRoute : Route {
        @Serializable
        data object Main : AssetRoute
    }

    @Serializable
    sealed interface InsightRoute : Route {
        @Serializable
        data object Main : InsightRoute


    }

    @Serializable
    sealed interface SettingsRoute : Route {
        @Serializable
        data object Main : SettingsRoute
    }


    @Serializable
    sealed interface CategorRoute : Route {
        @Serializable
        data object Main : CategorRoute

        @Serializable
        data class Detail(val kategoriId: Long) : CategorRoute

        @Serializable
        data object AddNew : CategorRoute
    }

}