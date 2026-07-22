package com.example.nexcashapplication.feature.transaction.presentation.addnew

data class AddTransactionState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amountDisplay: String = "0",
    val category: String? = null,
    val account: String? = null,
    val date: String = "24 May 2024",
    val time: String = "14:30",
    val note: String = "",
    val attachmentName: String? = null,
    val isRecurring: Boolean = false
) {
    // Used to enable/disable the Save button
    val isValid: Boolean
        get() = amountDisplay != "0" &&
                category != null &&
                account != null
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

