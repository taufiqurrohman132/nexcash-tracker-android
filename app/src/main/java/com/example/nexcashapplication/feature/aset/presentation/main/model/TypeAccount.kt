package com.example.nexcashapplication.feature.aset.presentation.main.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R

// domain/model/TypeAccount.kt
enum class TypeAccount(@StringRes val labelResId: Int) {
    CASH(R.string.account_type_cash),
    BANK(R.string.account_type_bank),
    CARD(R.string.account_type_card),
    DEBIT_CARD(R.string.account_type_debit_card),
    SAVINGS(R.string.account_type_savings),
    PREPAID_CARD(R.string.account_type_prepaid_card),
    INVESTMENT(R.string.account_type_investment),
    MINES_ACCOUNT(R.string.account_type_mines_account),
    LOAN(R.string.account_type_loan),
    INSURANCE(R.string.account_type_insurance),
    OTHER(R.string.account_type_other)
}