package com.example.nexcashapplication.feature.dashboard.presentation.ui

sealed interface DashboardEffect {
    data object NavigateToNotifications : DashboardEffect
    data object NavigateToProfile : DashboardEffect
    data object NavigateToTransfer : DashboardEffect
    data object NavigateToTagihan : DashboardEffect
    data object NavigateToScan : DashboardEffect
    data object NavigateToMoreMenu : DashboardEffect
    data object NavigateToBudgetDetail : DashboardEffect
    data object NavigateToAllTransactions : DashboardEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DashboardEffect
    data object NavigateToAddTransaction : DashboardEffect
    data class ShowError(val message: String) : DashboardEffect
}