package com.example.nexcashapplication.feature.transaction.presentation.addnew

sealed interface AddEditTransactionEvent {

    data object OnBackClick : AddEditTransactionEvent

    data class OnTypeChange(
        val type: TransactionType
    ) : AddEditTransactionEvent

    data object OnAmountClick : AddEditTransactionEvent

    data object OnCategoryClick : AddEditTransactionEvent

    data object OnAccountClick : AddEditTransactionEvent

    data object OnDateClick : AddEditTransactionEvent

    data object OnTimeClick : AddEditTransactionEvent

    data class OnNoteChange(
        val note: String
    ) : AddEditTransactionEvent

    data class OnNameTransactionChange(
        val name: String
    ) : AddEditTransactionEvent

    data object OnAttachmentClick : AddEditTransactionEvent

    data class OnRecurringChange(
        val isRecurring: Boolean
    ) : AddEditTransactionEvent

    data object OnSaveClick : AddEditTransactionEvent
}