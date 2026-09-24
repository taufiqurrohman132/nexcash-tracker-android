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
        data class AddEdit(val transactionId: String? = null,val accountId: String? = null,val categoryId: String? = null,) : TransactionRoute

        /**
         * Halaman Semua Transaksi — dibuka dari berbagai titik "Lihat Semua"
         * (detail kategori, detail akun, dst.) dengan filter berbeda (ADR-002).
         *
         * Filter diwakili **argumen primitif** (`categoryId`/`accountId`), bukan objek
         * sealed, karena inferensi NavType Navigation hanya menutup tipe primitif —
         * objek sealed membuat graph NavHost gagal dibangun saat start. Pemetaan ke
         * [com.example.nexcashapplication.feature.transaction.domain.model.TransactionFilter]
         * dilakukan lewat `TransactionFilter.fromRoute(...)` di ViewModel.
         * Keduanya `null` = tanpa filter (seluruh transaksi).
         */
        @Serializable
        data class All(
            val categoryId: String? = null,
            val accountId: String? = null,
        ) : TransactionRoute
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

        @Serializable
        data object EditProfile : SettingsRoute

        @Serializable
        data object InformasiPribadi : SettingsRoute

        @Serializable
        data object KeamananAkun : SettingsRoute

        @Serializable
        data object MataUang : SettingsRoute

        @Serializable
        data object Bahasa : SettingsRoute

        @Serializable
        data object TemaWarna : SettingsRoute

        @Serializable
        data object Notifikasi : SettingsRoute

        @Serializable
        data object PusatBantuan : SettingsRoute
    }


    @Serializable
    sealed interface CategoryRoute : Route {
        @Serializable
        data object Main : CategoryRoute

        @Serializable
        data class Detail(val categoryId: String) : CategoryRoute

        @Serializable
        data class SubCategoryList(val categoryId: String, val categoryName: String) : CategoryRoute

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
        data class AddEdit(val budgetId: String? = null, val categoryId: String? = null) :
            BudgetRoute

        /** Edit alokasi anggaran banyak kategori sekaligus (EditPresentageBudgetScreen). */
        @Serializable
        data object EditMultiple : BudgetRoute
    }

    @Serializable
    sealed interface StatisticRoute : Route {
        @Serializable
        data object Main : StatisticRoute


    }


}