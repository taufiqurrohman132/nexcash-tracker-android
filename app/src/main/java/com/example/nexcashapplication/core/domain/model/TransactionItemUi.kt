package com.example.nexcashapplication.core.domain.model

data class TransactionItemUi(
    val id: String,
    val title: String,
    val subtitle: String,   // nama bank/wallet
    val time: String,       // "10:45"
    val amount: Long,
    val isIncome: Boolean,
    val category: String,
    val iconRes: Int        // icon kategori
)
