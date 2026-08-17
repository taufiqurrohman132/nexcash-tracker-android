package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.model.TypeAccountItem
import com.example.nexcashapplication.ui.theme.PrimaryCyan

fun TypeAccount.toLabel(): String = when (this) {
    TypeAccount.CASH -> "Tunai"
    TypeAccount.BANK -> "Bank"
    TypeAccount.CARD -> "Kartu"
    TypeAccount.DEBIT_CARD -> "Kartu Debit"
    TypeAccount.SAVINGS -> "Tabungan"
    TypeAccount.PREPAID_CARD -> "Kartu Prabayar"
    TypeAccount.INVESTMENT -> "Investasi"
    TypeAccount.EWALLET -> "E-Wallet"
    TypeAccount.LOAN -> "Pinjaman"
    TypeAccount.INSURANCE -> "Asuransi"
    TypeAccount.OTHER -> "Lainnya"
}

fun TypeAccount.toUiItem(): TypeAccountItem = when (this) {
    TypeAccount.CASH -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.Payments,
        iconColor = Color(0xFF4CAF50), // Green
    )

    TypeAccount.BANK -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.AccountBalance,
        iconColor = PrimaryCyan,
    )

    TypeAccount.CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CreditCard,
        iconColor = Color(0xFF00BCD4), // Cyan
    )

    TypeAccount.DEBIT_CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CreditCard,
        iconColor = Color(0xFF2196F3), // Blue
    )

    TypeAccount.SAVINGS -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.Savings,
        iconColor = Color(0xFFE91E63), // Pink/Magenta
    )

    TypeAccount.PREPAID_CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CardGiftcard,
        iconColor = Color(0xFF9E9E9E), // Gray
    )

    TypeAccount.INVESTMENT -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.TrendingUp,
        iconColor = Color(0xFF673AB7), // Deep Purple
    )

    TypeAccount.EWALLET -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.AccountBalanceWallet,
        iconColor = Color(0xFFFFC107), // Amber
    )

    TypeAccount.LOAN -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.RequestQuote,
        iconColor = Color(0xFFF44336), // Red
    )

    TypeAccount.INSURANCE -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.HealthAndSafety,
        iconColor = Color(0xFF009688), // Teal
    )

    TypeAccount.OTHER -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.HelpOutline,
        iconColor = Color(0xFF607D8B), // Blue Grey
    )
}

fun getAllTypeAccountItems(): List<TypeAccountItem> =
    TypeAccount.entries.map { it.toUiItem() }
