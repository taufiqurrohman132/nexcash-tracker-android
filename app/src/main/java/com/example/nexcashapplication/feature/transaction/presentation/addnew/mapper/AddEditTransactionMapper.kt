package com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AddEditTransactionState
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryAndSubModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel

fun Category.toTransactionCategoryItemModel() = TransactionCategoryItemModel(
    id = id,
    name = name,
    tipe = type,
    icon = CategoryIcons.fromId(iconId),
    iconColor = color.toColor(),
)

fun CategoryAndSubcategory.toUIModel() = TransactionCategoryAndSubModel(
    categoriItem = category.toTransactionCategoryItemModel(),
    subCategoriesItem = subCategories.map { it.toTransactionCategoryItemModel() },
)

fun TransactionWithDetails.toAddEditTransactionState(
    availableCategories: List<TransactionCategoryAndSubModel>,
    availableAccounts: List<AccountUiModel>,
) = AddEditTransactionState(
    transactionId = transaction.id,
    type = transaction.typeTransaction,
    amount = transaction.amount,
    nameTransaction = transaction.title,
    selectedCategoryId = transaction.categoryId,
    selectedAccountId = transaction.accountId,
    transactionAt = transaction.dateTime,
    note = transaction.description ?: "",
    availableCategories = availableCategories,
    availableAccounts = availableAccounts,
//    attachmentName = transaction.,
//    isRecurring = transaction.isRecurring
)