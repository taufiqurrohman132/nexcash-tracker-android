package com.example.nexcashapplication.feature.dashboard.presentation.event

sealed interface DashboardEvent {
    data object OnScreenLoad : DashboardEvent
    data object OnRefresh : DashboardEvent
    data object OnToggleBalanceVisibility : DashboardEvent
    data object OnNotificationClick : DashboardEvent
    data object OnProfileClick : DashboardEvent

    // Quick action — hanya fitur yang benar-benar ada (punya route terdaftar).
    // Transfer/Tagihan/Scan/Lainnya dibuang: tidak ada fiturnya, tombolnya mati.
    data object OnKategoriClick : DashboardEvent
    data object OnTambahAkunClick : DashboardEvent
    data object OnStatistikClick : DashboardEvent

    data object OnLihatDetailBudgetClick : DashboardEvent
    data object OnSemuaTransaksiClick : DashboardEvent
    data class OnTransactionClick(val transactionId: String) : DashboardEvent
    data object OnAddTransactionClick : DashboardEvent
}
