package com.example.nexcashapplication.feature.aset.data.dummy

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount

// data/dummy/DummyData.kt
object DummyData {

    val accounts = listOf(
        AccountModel(
            id = "acc-001",
            accountType = TypeAccount.CASH,
            name = "Dompet Utama",
            institution = null,
            accountNumber = null,
            currentBalance = 2_500_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-002",
            accountType = TypeAccount.BANK,
            name = "BCA Tabungan",
            institution = "BCA",
            accountNumber = "1234567890",
            currentBalance = 15_750_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-003",
            accountType = TypeAccount.PREPAID_CARD,
            name = "GoPay",
            institution = "Gojek",
            accountNumber = "081234567890",
            currentBalance = 350_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-004",
            accountType = TypeAccount.BANK,
            name = "OVO",
            institution = "OVO",
            accountNumber = "081234567891",
            currentBalance = 125_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-005",
            accountType = TypeAccount.BANK,
            name = "BSI Syariah",
            institution = "BSI",
            accountNumber = "9876543210",
            currentBalance = 8_000_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-006",
            accountType = TypeAccount.INSURANCE,
            name = "BCA Credit Card",
            institution = "BCA",
            accountNumber = "4111111111111234",
            currentBalance = -2_300_000L, // minus = hutang kartu kredit
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-007",
            accountType = TypeAccount.INVESTMENT,
            name = "Bibit Reksa Dana",
            institution = "Bibit",
            accountNumber = null,
            currentBalance = 5_200_000L,
            isActive = true,
            deletedAt = null,
        ),
        AccountModel(
            id = "acc-008",
            accountType = TypeAccount.BANK,
            name = "Mandiri Lama",
            institution = "Mandiri",
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
