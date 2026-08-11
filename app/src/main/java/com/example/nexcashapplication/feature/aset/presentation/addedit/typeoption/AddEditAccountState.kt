package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel

@Immutable
data class AddEditAccountState(
    val isLoading: Boolean = false,
    val totalBalance: Long = 0L,
    val isBalanceVisible: Boolean = true,
    val monthlyChangePercent: Double = 0.0,
    val activeAssetCount: Int = 0,
    val bankAccounts: List<AccountUiModel> = emptyList(),
    val eWallets: List<AccountUiModel> = emptyList(),
//    val investments: List<InvestmentUiModel> = emptyList(),
    val error: String? = null,
)