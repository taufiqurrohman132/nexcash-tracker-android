package com.example.nexcashapplication.feature.aset.presentation.detail

sealed interface DetailAccountEvent {
    data object OnBackClick : DetailAccountEvent
    data object OnSettingsClick : DetailAccountEvent
    data object OnTransaksiClick : DetailAccountEvent
    data object OnTransferClick : DetailAccountEvent
    data object OnLihatSemuaClick : DetailAccountEvent
    data class OnTransactionClick(val transactionId: String) : DetailAccountEvent
    data object OnUbahNamaAsetClick : DetailAccountEvent
    data object OnHapusAsetClick : DetailAccountEvent
    data object OnConfirmDelete : DetailAccountEvent
    data object OnDismissDeleteDialog : DetailAccountEvent
}