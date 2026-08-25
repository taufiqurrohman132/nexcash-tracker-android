package com.example.nexcashapplication.feature.account.presentation.main

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiGroupModel
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel

@Immutable
data class AccountAssetState(
    val isLoading: Boolean = false,
    val totalBalance: Long = 0L,
    val isBalanceVisible: Boolean = true,
    val monthlyChangePercent: Double = 0.0,
    val activeAssetCount: Int = 0,
    val bankAccounts: List<AccountUiGroupModel> = emptyList(),
    val eWallets: List<AccountUiModel> = emptyList(),
//    val investments: List<InvestmentUiModel> = emptyList(),
    val error: String? = null,
)