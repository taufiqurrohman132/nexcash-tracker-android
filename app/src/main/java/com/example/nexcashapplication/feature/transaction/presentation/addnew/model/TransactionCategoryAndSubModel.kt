package com.example.nexcashapplication.feature.transaction.presentation.addnew.model

data class TransactionCategoryAndSubModel(
    val categoriItem: TransactionCategoryItemModel,
    val subCategoriesItem: List<TransactionCategoryItemModel>,
)
