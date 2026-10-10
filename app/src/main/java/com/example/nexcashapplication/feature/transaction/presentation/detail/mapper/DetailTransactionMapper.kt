package com.example.nexcashapplication.feature.transaction.presentation.detail.mapper

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.feature.account.presentation.main.mapper.toAccountUiModel
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.presentation.detail.model.DetailTransactionUiModel
import com.example.nexcashapplication.feature.transaction.presentation.detail.state.DeleteTransactionDialogUiState


fun TransactionWithDetails.toDetailTransactionUiModel(): DetailTransactionUiModel {
    return DetailTransactionUiModel(
        transactionId = transaction.id,
        name = transaction.title,
        amount = transaction.amount,
        type = transaction.typeTransaction,
        dateTime = transaction.dateTime,
        category = category?.toUiModel(),   // asumsi ada mapper ini; ganti kalau udah UI model
//        status = transaction.status,        // asumsi ada field status di domain; sesuaikan
        account = account?.toAccountUiModel(),      // sama, asumsi ada mapper
        note = transaction.description,
        attachment = null,                  // TODO: map kalau TransactionWithDetails udah punya data attachment
    )
}

// dialog
fun DetailTransactionUiModel.toDeleteDialogUiState(): DeleteTransactionDialogUiState {
    val accountName = account?.name ?: ""
    val formattedAmount = amount.formatCurrency()
    return DeleteTransactionDialogUiState(
        title = name,
        formattedAmount = formattedAmount,
        // label tipe: kunci resource (bukan `type.name` mentah) — di-resolve di Composable
        typeLabelRes = type.labelResId,
        accountName = accountName,
        balanceEffectText = if (type == TypeCategory.EXPENSE) {
            uiText(R.string.tx_balance_effect_expense, accountName, formattedAmount)
        } else {
            uiText(R.string.tx_balance_effect_income, accountName, formattedAmount)
        },
    )
}
