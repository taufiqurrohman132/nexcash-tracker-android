package com.example.nexcashapplication.feature.account.presentation.detail

sealed interface DetailAccountEvent {
    data object OnBackClick : DetailAccountEvent
    data object OnSettingsClick : DetailAccountEvent
    data object OnAddTransactionClick : DetailAccountEvent
    data object OnLihatSemuaClick : DetailAccountEvent
    data class OnTransactionClick(val transactionId: String?) : DetailAccountEvent
    data object OnEditAccountClick : DetailAccountEvent
    data object OnDeleteAccountClick : DetailAccountEvent
    data object OnConfirmDelete : DetailAccountEvent
    data object OnDismissDeleteDialog : DetailAccountEvent

    /** Muat ulang detail akun setelah gagal memuat (tombol "Coba lagi"). */
    data object OnRetryLoad : DetailAccountEvent
}