package com.example.nexcashapplication.feature.dashboard.presentation.event

sealed interface DashboardEvent {

    data object ToggleBalanceVisibility : DashboardEvent

    data object TransferClicked : DashboardEvent

    data object BillClicked : DashboardEvent

    data object ScanClicked : DashboardEvent

    data object MoreClicked : DashboardEvent

    data class TransactionClicked(
        val id: Int
    ) : DashboardEvent
}