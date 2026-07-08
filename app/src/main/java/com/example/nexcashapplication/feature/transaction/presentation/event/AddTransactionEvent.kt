package com.example.nexcashapplication.feature.transaction.presentation.event

import com.example.nexcashapplication.feature.transaction.presentation.state.TransactionType

sealed interface AddTransactionEvent {

    data object OnBackClick : AddTransactionEvent

    data class OnTypeChange(
        val type: TransactionType
    ) : AddTransactionEvent

    data object OnAmountClick : AddTransactionEvent

    data object OnCategoryClick : AddTransactionEvent

    data object OnAccountClick : AddTransactionEvent

    data object OnDateClick : AddTransactionEvent

    data object OnTimeClick : AddTransactionEvent

    data class OnNoteChange(
        val note: String
    ) : AddTransactionEvent

    data object OnAttachmentClick : AddTransactionEvent

    data class OnRecurringChange(
        val isRecurring: Boolean
    ) : AddTransactionEvent

    data object OnSaveClick : AddTransactionEvent
}