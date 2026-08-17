package com.example.nexcashapplication.feature.transaction.presentation.main.model

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel

data class TransactionUiGroupModel(
    val header: TransactionHeaderUiModel,
    val listTransaction: List<TransactionItemUiModel>,
)

