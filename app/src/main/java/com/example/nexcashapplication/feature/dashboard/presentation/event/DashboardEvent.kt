package com.example.nexcashapplication.feature.dashboard.presentation.event

sealed interface DashboardEvent {
    data object OnScreenLoad : DashboardEvent
    data object OnRefresh : DashboardEvent
    data object OnToggleBalanceVisibility : DashboardEvent
    data object OnNotificationClick : DashboardEvent
    data object OnProfileClick : DashboardEvent

    data object OnTransferClick : DashboardEvent
    data object OnTagihanClick : DashboardEvent
    data object OnScanClick : DashboardEvent
    data object OnLainnyaClick : DashboardEvent

    data object OnLihatDetailBudgetClick : DashboardEvent
    data object OnSemuaTransaksiClick : DashboardEvent
    data class OnTransactionClick(val transactionId: String) : DashboardEvent
    data object OnAddTransactionClick : DashboardEvent
}