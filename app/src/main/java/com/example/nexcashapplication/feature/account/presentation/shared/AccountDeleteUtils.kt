package com.example.nexcashapplication.feature.account.presentation.shared

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision


fun AccountModel.resolveDeleteState(transactionCount: Int): DeleteAccountDesision = when {
    currentBalance != 0L -> DeleteAccountDesision.Blocked(currentBalance)
    transactionCount > 0 -> DeleteAccountDesision.ConfirmWithHistory(transactionCount)
    else -> DeleteAccountDesision.ConfirmSimple
}