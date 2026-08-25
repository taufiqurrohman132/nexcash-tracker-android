package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryAndSubModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel
import kotlin.time.Clock
import kotlin.time.Instant


data class AddEditTransactionState(
    val transactionId: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Long = 0L,
    val nameTransaction: String = "",
    val selectedCategoryId: String? = null,
    val selectedAccountId: String? = null,
    val transactionAt: Instant = Clock.System.now(),
    val note: String = "",
    val attachmentName: String? = null,
    val isRecurring: Boolean = false,

    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,

    val showCategoryBottomSheet: Boolean = false,
    val showAccountBottomSheet: Boolean = false,

    val availableCategories: List<TransactionCategoryAndSubModel> = emptyList(),
//    val availableSubCategories: List<TransactionCategoryItemModel> = emptyList(),
    val availableAccounts: List<AccountUiModel> = emptyList(),
) {
    // Used to enable/disable the Save button
    val isValid: Boolean
        get() = nameTransaction.isNotBlank() &&
                amount > 0L &&
                selectedCategoryId != null &&
                selectedAccountId != null

    val subCategories: List<TransactionCategoryItemModel>
        get() = availableCategories.find { it.categoriItem.id == selectedCategoryId }
                ?.subCategoriesItem ?: emptyList()

    val selectedCategory: TransactionCategoryItemModel?
        get() = availableCategories.flatMap {
            listOf(it.categoriItem) + it.subCategoriesItem
        }.find { it.id == selectedCategoryId }

    val selectedAccount: AccountUiModel?
        get() = availableAccounts.find { it.id == selectedAccountId }
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

