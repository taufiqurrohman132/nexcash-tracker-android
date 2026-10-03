package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel

sealed interface AddEditTransactionEvent {

    data object OnBackClick : AddEditTransactionEvent

    data class OnTypeChange(
        val type: TransactionType,
    ) : AddEditTransactionEvent


    /**
     * Buka/tutup sheet Category.
     *
     * @param autoAdvance kalau `true`, setelah kategori **selesai** dipilih (sheet
     * tertutup) sheet Account langsung dibuka otomatis — jalur cepat dari tombol
     * Next keyboard. Tap biasa / tutup pakai scrim = `false` jadi tidak lanjut.
     */
    data class OnCategoryClick(
        val autoAdvance: Boolean = false,
    ) : AddEditTransactionEvent
    data class OnCategorySelected(
        val category: TransactionCategoryItemModel,
    ) : AddEditTransactionEvent

    data object OnAccountClick : AddEditTransactionEvent
    data class OnAccountSelected(
        val account: AccountUiModel,
    ) : AddEditTransactionEvent

    data object OnDateClick : AddEditTransactionEvent
    data class OnDateChange(
        val date: Long,
    ) : AddEditTransactionEvent

    data object OnTimeClick : AddEditTransactionEvent

    /**
     * Bawa jam saja (bukan `Instant`): tanggalnya diambil dari state saat ini di
     * ViewModel, supaya memilih jam tidak mereset tanggal ke hari ini.
     */
    data class OnTimeChange(
        val hour: Int,
        val minute: Int,
    ) : AddEditTransactionEvent

    data class OnNoteChange(
        val note: String,
    ) : AddEditTransactionEvent

    data class OnNameTransactionChange(
        val name: String,
    ) : AddEditTransactionEvent

    data class OnAmountChange(
        val amount: String,
    ) : AddEditTransactionEvent

    data object OnAttachmentClick : AddEditTransactionEvent

    data class OnRecurringChange(
        val isRecurring: Boolean,
    ) : AddEditTransactionEvent

    data object OnSaveClick : AddEditTransactionEvent
}