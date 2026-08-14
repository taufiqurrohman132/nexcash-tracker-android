package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.core.domain.model.TransactionItemUi
import com.example.nexcashapplication.feature.aset.domain.model.DeleteAccountDesision

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
    val deleteDialogDesision: DeleteAccountDesision? = null,
    val errorMessage: String? = null,
)