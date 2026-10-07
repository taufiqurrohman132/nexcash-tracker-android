package com.example.nexcashapplication.feature.transaction.presentation.detail.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlin.time.Instant

// presentation/detail/model/TransactionDetailUiModel.kt
data class DetailTransactionUiModel(
    val transactionId: String?,
    val name: String,
    val amount: Long,
    val type: TransactionType, // pindah ke domain.model
    val dateTime: Instant,
    val category: CategoryUiItemModel?,      // bukan Category domain langsung
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

    /**
     * Label baris sumber dana — kunci string resource, bukan teks mentah.
     * Resolve di UI dengan `stringResource(accountLabelRes)`, jadi ikut bahasa aktif.
     */
    @get:StringRes
    val accountLabelRes: Int
        get() = when (type) {
            TransactionType.INCOME -> R.string.tx_received_at
            TransactionType.EXPENSE -> R.string.tx_funding_source
        }
    // amountColor DIHAPUS dari sini — resolve warnanya di Composable pakai token tema,
    // bukan di-derive di model. Model nggak boleh tahu soal Color Compose sama sekali.
}