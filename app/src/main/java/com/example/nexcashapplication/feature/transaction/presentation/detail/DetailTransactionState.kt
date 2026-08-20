package com.example.nexcashapplication.feature.transaction.presentation.detail

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.feature.transaction.presentation.detail.model.AttachmentUiModel
import kotlin.time.Clock
import kotlin.time.Instant

data class DetailTransactionState(
    val transactionId: String = "",
    val name: String = "",           // "Gojek - Transportasi"
    val amount: Long = 0L,
    val type: TransactionType = TransactionType.EXPENSE,   // INCOME / EXPENSE — dari domain enum, bukan dihardcode di sini
    val dateTime: Instant = Clock.System.now(),
    val category: Category? = null,
    val status: String = "",      // "Berhasil"
    val account: AccountModel? = null,
    val note: String? = "",
    val isDeleting: Boolean = false,
    val attachment: AttachmentUiModel? = null,
) {
    // Derived, bukan disimpan manual — hindari state jadi out of sync
    val signedAmountText: String
        get() = when (type) {
            TransactionType.INCOME -> "+ ${amount.formatCurrency()}"
            TransactionType.EXPENSE -> "- ${amount.formatCurrency()}"
        }

    val amountColor: Color
        get() = when (type) {
            TransactionType.INCOME -> Color.Green
            TransactionType.EXPENSE -> Color.Red
        }

    val accountLabel: String
        get() = when (type) {
            TransactionType.INCOME -> "Diterima di"
            TransactionType.EXPENSE -> "Sumber Dana"
        }
}