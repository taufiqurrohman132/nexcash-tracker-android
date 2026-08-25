package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel
import kotlin.time.Instant

sealed interface AddEditTransactionEvent {

    data object OnBackClick : AddEditTransactionEvent

    data class OnTypeChange(
        val type: TransactionType,
    ) : AddEditTransactionEvent


    data object OnCategoryClick : AddEditTransactionEvent
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
    data class OnTimeChange(
        val time: Instant,
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