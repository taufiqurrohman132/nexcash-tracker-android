package com.example.nexcashapplication.feature.aset.domain.model

import com.example.nexcashapplication.feature.aset.presentation.model.TypeAccount

data class AccountModel(

    val id: String = "",
    // contoh: cash, bank, ewallet, credit_card, investment
    val accountType: TypeAccount,
    val name: String,
    val institution: String? = null,
    val accountNumber: String? = null,
    val currentBalance: Long = 0,
    val isActive: Boolean = true,
    val deletedAt: Long? = null,
)

