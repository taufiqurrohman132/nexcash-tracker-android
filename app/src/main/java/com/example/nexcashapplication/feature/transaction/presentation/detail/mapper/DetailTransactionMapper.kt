package com.example.nexcashapplication.feature.transaction.presentation.detail.mapper

import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.aset.presentation.main.mapper.toAccountUiModel
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
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
    val effectVerb = if (type == TransactionType.EXPENSE) "bertambah" else "berkurang"
    val formattedAmount = amount.formatCurrency()
    return DeleteTransactionDialogUiState(
        title = name,
        formattedAmount = formattedAmount,
        typeLabel = type.name, // masih perlu lo putusin: mentah atau di-lokalisasi?
        accountName = account?.name ?: "",
        balanceEffectText = "Saldo akun \"${account?.name}\" akan $effectVerb $formattedAmount setelah transaksi ini dihapus.",
    )
}