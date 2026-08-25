package com.example.nexcashapplication.feature.account.presentation.detail

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel

@Immutable
data class DetailAccountState(
    val assetId: String? = null,
    val isLoading: Boolean = false,
    val assetName: String = "",
    val typeAccount: TypeAccount = TypeAccount.OTHER,
    val providers: AccountProviderUiModel? = null,
    val balance: Long = 0L,
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),

    val showDeleteConfirmation: Boolean = false,
    val deleteDialogDesision: DeleteAccountDesision? = null,
    val errorMessage: String? = null,
)