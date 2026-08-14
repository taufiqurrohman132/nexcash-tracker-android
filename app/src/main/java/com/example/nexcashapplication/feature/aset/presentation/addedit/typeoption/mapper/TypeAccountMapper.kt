package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.FoodBank
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.model.TypeAccountItem
import com.example.nexcashapplication.ui.theme.PrimaryCyan

// presentation/mapper/TypeAccountMapper.kt
fun TypeAccount.toUiItem(): TypeAccountItem = when (this) {
    TypeAccount.CASH -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.Payments,
        iconColor = Color.Green,
    )

    TypeAccount.BANK -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.AccountBalance,
        iconColor = PrimaryCyan,
    )

    TypeAccount.CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CreditCard,
        iconColor = Color.Cyan,
    )

    TypeAccount.DEBIT_CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CreditCard,
        iconColor = Color.Blue,
    )

    TypeAccount.SAVINGS -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.Savings,
        iconColor = Color.Magenta,
    )

    TypeAccount.PREPAID_CARD -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.CardGiftcard,
        iconColor = Color.LightGray,
    )

    TypeAccount.INVESTMENT -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.TrendingUp,
        iconColor = Color.Blue,
    )

    TypeAccount.EWALLET -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.AccountBalanceWallet,
        iconColor = Color.Yellow,
    )

    TypeAccount.LOAN -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.RequestQuote,
        iconColor = Color.Red,
    )

    TypeAccount.INSURANCE -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.HealthAndSafety,
        iconColor = Color.Magenta,
    )

    TypeAccount.OTHER -> TypeAccountItem(
        type = this,
        icon = Icons.Outlined.FoodBank,
        iconColor = Color.Gray,
    )
}

// helper buat generate semua item sekaligus, misal buat dropdown/bottom sheet
fun getAllTypeAccountItems(): List<TypeAccountItem> =
    TypeAccount.entries.map { it.toUiItem() }