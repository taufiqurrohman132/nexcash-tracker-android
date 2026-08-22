package com.example.nexcashapplication.feature.transaction.presentation.detail.model

import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryUiModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlin.time.Instant

// presentation/detail/model/TransactionDetailUiModel.kt
data class DetailTransactionUiModel(
    val transactionId: String?,
    val name: String,
    val amount: Long,
    val type: TransactionType, // pindah ke domain.model
    val dateTime: Instant,
    val category: CategoryUiModel?,      // bukan Category domain langsung
//    val status: TransactionStatus,      // enum, bukan String
    val account: AccountUiModel?,        // bukan AccountModel domain langsung
    val note: String?,
    val attachment: AttachmentUiModel?,
) {
    val signedAmountText: String
        get() = when (type) {
            TransactionType.INCOME -> "+ ${amount.formatCurrency()}"
            TransactionType.EXPENSE -> "- ${amount.formatCurrency()}"
        }

    val accountLabel: String
        get() = when (type) {
            TransactionType.INCOME -> "Diterima di"
            TransactionType.EXPENSE -> "Sumber Dana"
        }
    // amountColor DIHAPUS dari sini — resolve warnanya di Composable pakai token tema,
    // bukan di-derive di model. Model nggak boleh tahu soal Color Compose sama sekali.
}