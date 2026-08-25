package com.example.nexcashapplication.feature.account.domain.model

import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel

data class AccountModel(

    val id: String? = null,
    // contoh: cash, bank, ewallet, credit_card, investment
    val accountType: TypeAccount,
    val name: String,
    val providerAccount: AccountProviderUiModel? = null,
    val currentBalance: Long = 0,
    val isActive: Boolean = true,
    val deletedAt: Long? = null,
    val lastUpdateAt: Long = 0L,
)

