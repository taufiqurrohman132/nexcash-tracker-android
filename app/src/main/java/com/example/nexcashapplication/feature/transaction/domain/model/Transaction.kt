package com.example.nexcashapplication.feature.transaction.domain.model

import com.example.nexcashapplication.core.domain.model.TypeCategory
import kotlin.time.Clock
import kotlin.time.Instant


data class Transaction(
    val id: String? = null,
    val title: String,
    val amount: Long,
    val categoryId: String?,
    val description: String?,
    val dateTime: Instant = Clock.System.now(),
    val typeTransaction: TypeCategory,
    val accountId: String?,
) {
    fun applyAmount(): Long {
        return when (typeTransaction) {
            TypeCategory.INCOME -> amount
            TypeCategory.EXPENSE -> -amount
        }
    }

    fun reverseAmount(): Long {
        return -applyAmount()
    }
}
