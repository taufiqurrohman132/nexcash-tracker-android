package com.example.nexcashapplication.feature.transaction.presentation.addnew

data class AddEditTransactionState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amountDisplay: Long = 0L,
    val nameTransaction: String = "",
    val category: String? = null,
    val account: String? = null,
    val date: String = "24 May 2024",
    val time: String = "14:30",
    val note: String = "",
    val attachmentName: String? = null,
    val isRecurring: Boolean = false,
) {
    // Used to enable/disable the Save button
    val isValid: Boolean
        get() = amountDisplay != 0L &&
                category != null &&
                account != null
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

