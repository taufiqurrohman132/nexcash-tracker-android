package com.example.nexcashapplication.feature.account.presentation.main.model

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel


@Immutable
data class AccountUiModel(
    val id: String? = null,
    val name: String,
    val provider: AccountProviderUiModel?,
    val balance: Long,
    val iconUrl: String? = null,
)