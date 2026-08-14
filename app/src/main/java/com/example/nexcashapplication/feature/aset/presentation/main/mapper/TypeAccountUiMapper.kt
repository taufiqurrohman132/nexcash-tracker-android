package com.example.nexcashapplication.feature.aset.presentation.main.mapper

import androidx.annotation.StringRes
import com.example.nexcashapplication.R
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount

// ui/mapper/TypeAccountUiMapper.kt — UI layer, boleh depend ke Android resource
@get:StringRes
val TypeAccount.labelResId: Int
    get() = when (this) {
        TypeAccount.CASH -> R.string.account_type_cash
        TypeAccount.BANK -> R.string.account_type_bank
        TypeAccount.CARD -> R.string.account_type_card
        TypeAccount.DEBIT_CARD -> R.string.account_type_debit_card
        TypeAccount.SAVINGS -> R.string.account_type_savings
        TypeAccount.PREPAID_CARD -> R.string.account_type_prepaid_card
        TypeAccount.INVESTMENT -> R.string.account_type_investment
        TypeAccount.EWALLET -> R.string.account_type_e_wallet
        TypeAccount.LOAN -> R.string.account_type_loan
        TypeAccount.INSURANCE -> R.string.account_type_insurance
        TypeAccount.OTHER -> R.string.account_type_other
    }