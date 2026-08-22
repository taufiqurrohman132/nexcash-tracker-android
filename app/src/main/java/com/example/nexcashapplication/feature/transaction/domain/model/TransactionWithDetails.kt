package com.example.nexcashapplication.feature.transaction.domain.model

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel

data class TransactionWithDetails(
    val transaction: Transaction,
    val account: AccountModel? = null,
    val category: Category? = null,
) {

}
