package com.example.nexcashapplication.feature.dashboard.presentation.ui

import com.example.nexcashapplication.core.i18n.UiText

sealed interface DashboardEffect {
    data object NavigateToNotifications : DashboardEffect
    data object NavigateToProfile : DashboardEffect

    /** Kelola kategori (Route.CategoryRoute.Main) — satu-satunya pintu cepat ke sana. */
    data object NavigateToCategories : DashboardEffect

    /** Tambah akun (Route.AccountRoute.AddEditTypeAccount). */
    data object NavigateToAddAccount : DashboardEffect

    /** Statistik/insight (Route.StatisticRoute.Main). */
    data object NavigateToStatistics : DashboardEffect

    data object NavigateToBudgetDetail : DashboardEffect
    data object NavigateToAllTransactions : DashboardEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DashboardEffect
    data object NavigateToAddTransaction : DashboardEffect
    data class ShowError(val message: UiText) : DashboardEffect
}
