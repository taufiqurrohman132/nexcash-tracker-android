package com.example.nexcashapplication.feature.aset.data.dummy

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderItem
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount

// data/dummy/DummyData.kt
object DummyData {

    val accounts = listOf(
        AccountModel(
            id = "acc-001",
            accountType = TypeAccount.CASH,
            name = "Dompet Utama",
            providerAccount = null,
            accountNumber = null,
            currentBalance = 2_500_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-002",
            accountType = TypeAccount.BANK,
            name = "BCA Tabungan",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = "1234567890",
            currentBalance = 15_750_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-003",
            accountType = TypeAccount.PREPAID_CARD,
            name = "GoPay",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = "081234567890",
            currentBalance = 350_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-004",
            accountType = TypeAccount.BANK,
            name = "OVO",
            providerAccount = AccountProviderItem("bca", "BCA"),

            accountNumber = "081234567891",
            currentBalance = 125_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-005",
            accountType = TypeAccount.BANK,
            name = "BSI Syariah",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = "9876543210",
            currentBalance = 8_000_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-006",
            accountType = TypeAccount.INSURANCE,
            name = "BCA Credit Card",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = "4111111111111234",
            currentBalance = -2_300_000L, // minus = hutang kartu kredit
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-007",
            accountType = TypeAccount.INVESTMENT,
            name = "Bibit Reksa Dana",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = null,
            currentBalance = 5_200_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-008",
            accountType = TypeAccount.BANK,
            name = "Mandiri Lama",
            providerAccount = AccountProviderItem("bca", "BCA"),
            accountNumber = "1122334455",
            currentBalance = 0L,
            isActive = false,
            deletedAt = System.currentTimeMillis(), // sudah dihapus/nonaktif
        ),
    )

    val monthlySummary = MonthlySummaryAccountResult(
        totalBalance = accounts
                .filter { it.isActive && it.deletedAt == null }
                .sumOf { it.currentBalance },
        activeAccount = accounts
                .count { it.isActive && it.deletedAt == null },
        monthlyIncome = 8_500_000L,
        monthlyExpense = 3_200_000L,
    )
}
