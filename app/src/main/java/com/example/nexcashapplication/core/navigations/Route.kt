package com.example.nexcashapplication.core.navigations

import com.example.nexcashapplication.core.domain.model.TypeCategory
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
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
    sealed interface CategoryRoute : Route {
        @Serializable
        data object Main : CategoryRoute

        @Serializable
        data class Detail(val categoryId: String) : CategoryRoute

        @Serializable
        data class AddNew(val categoryId: String, val parentId: String? = null) : CategoryRoute
        @Serializable
        data class Delete(val categoryId: String, val parentId: String? = null, val typeCategory: TypeCategory) : CategoryRoute
    }

}