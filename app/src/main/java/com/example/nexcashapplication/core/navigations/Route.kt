package com.example.nexcashapplication.core.navigations

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
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
        data class AddEdit(val transactionId: String? = null) : TransactionRoute
    }

    @Serializable
    sealed interface AccountRoute : Route {
        @Serializable
        data object Main : AccountRoute

        @Serializable
        data class Detail(val accountId: String?) : AccountRoute

        @Serializable
        data object AddEditTypeAccount : AccountRoute

        @Serializable
        data class AddEditInfo(
            val accountId: String? = null,
            val typeAccount: TypeAccount? = null,
        ) : AccountRoute
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
        data class AddNew(val categoryId: String? = null, val parentId: String? = null) :
            CategoryRoute

        @Serializable
        data class Delete(
            val categoryId: String,
            val parentId: String? = null,
            val typeCategory: TypeCategory,
        ) : CategoryRoute
    }

    @Serializable
    sealed interface BudgetRoute : Route {
        @Serializable
        data object Main : BudgetRoute

        @Serializable
        data class Detail(val budgetId: String) : BudgetRoute

        @Serializable
        data class AddEdit(val budgetId: String? = null, val categoryId: String? = null) : BudgetRoute
    }

    @Serializable
    sealed interface StatisticRoute : Route {
        @Serializable
        data object Main : StatisticRoute


    }


}