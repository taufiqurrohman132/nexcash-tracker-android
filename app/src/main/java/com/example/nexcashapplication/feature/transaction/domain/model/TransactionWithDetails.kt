package com.example.nexcashapplication.feature.transaction.domain.model

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.category.domain.model.Category

data class TransactionWithDetails(
    val transaction: Transaction,
    val account: AccountModel? = null,
    val category: Category? = null,
)
