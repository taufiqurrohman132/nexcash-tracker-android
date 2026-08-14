package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.core.domain.model.TransactionItemUi

@Immutable
data class DetailAccountState(
    val isLoading: Boolean = false,
    val assetId: String = "",
    val assetName: String = "",
    val bankName: String = "",
    val accountLabel: String = "", // e.g. "Tabungan"
    val balance: Long = 0L,
    val recentTransactions: List<TransactionItemUi> = emptyList(),
    val showDeleteConfirmation: Boolean = false,
    val errorMessage: String? = null,
)