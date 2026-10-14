package com.example.nexcashapplication.feature.dashboard.presentation.state

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.i18n.UiText

data class DashboardState(
    val userName: String = "Budi",
    val totalBalance: Long = 14560200,
    val income: Long = 12500000,
    val expense: Long = 3245000,
    val remainingBudget: Long = 1755000,
    val budgetUsedPercentage: Float = 0.65f,
    val transactions: List<TransactionItemUiModel> = emptyList(),
    // true sampai "transaksi terakhir" pertama masuk — frame pertama tanpa
    // cache menampilkan indikator, bukan kartu kosong
    val isLoading: Boolean = true,
    val isBalanceVisible: Boolean = true,
    /** Gagal memuat transaksi terakhir; null = tidak ada error. */
    val error: UiText? = null,
)
//
//data class TransactionUi(
//    val id: Int,
//    val title: String,
//    val amount: Long,
//    val category: String,
//    val date: String,
//    val isIncome: Boolean
//)