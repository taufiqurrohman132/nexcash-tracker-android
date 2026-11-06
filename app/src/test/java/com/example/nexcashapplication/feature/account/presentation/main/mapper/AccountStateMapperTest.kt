package com.example.nexcashapplication.feature.account.presentation.main.mapper

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
import com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.mapper.toLabelRes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountStateMapperTest {

    @Test
    fun `toAccountUiModel - memetakan AccountModel ke AccountUiModel dengan benar`() {
        val bcaProvider = ProviderCatalog.getById("bca")
        val model = AccountModel(
            id = "acc1",
            name = "BCA Utama",
            accountType = TypeAccount.BANK,
            providerAccount = bcaProvider,
            currentBalance = 5_000_000L,
        )

        val uiModel = model.toAccountUiModel()

        assertEquals("acc1", uiModel.id)
        assertEquals("BCA Utama", uiModel.name)
        assertEquals(bcaProvider, uiModel.provider)
        assertEquals(5_000_000L, uiModel.balance)
        assertEquals("", uiModel.iconUrl)
    }

    @Test
    fun `toAccountUiModelList - mengelompokkan berdasarkan tipe dan mengurutkan berdasarkan lastUpdateAt menurun`() {
        val bankOld = AccountModel(
            id = "b1",
            name = "Bank Lama",
            accountType = TypeAccount.BANK,
            currentBalance = 100_000L,
            lastUpdateAt = 1000L,
        )
        val bankNew = AccountModel(
            id = "b2",
            name = "Bank Baru",
            accountType = TypeAccount.BANK,
            currentBalance = 200_000L,
            lastUpdateAt = 5000L,
        )
        val ewallet = AccountModel(
            id = "w1",
            name = "OVO",
            accountType = TypeAccount.EWALLET,
            currentBalance = 50_000L,
            lastUpdateAt = 2000L,
        )

        val groups = listOf(bankOld, ewallet, bankNew).toAccountUiModelList()

        assertEquals(2, groups.size)

        val bankGroup = groups.first { it.nameGroupRes == TypeAccount.BANK.toLabelRes() }
        assertEquals(listOf("b2", "b1"), bankGroup.listAccount.map { it.id })

        val walletGroup = groups.first { it.nameGroupRes == TypeAccount.EWALLET.toLabelRes() }
        assertEquals(listOf("w1"), walletGroup.listAccount.map { it.id })
    }
}
