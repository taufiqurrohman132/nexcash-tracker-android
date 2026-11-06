package com.example.nexcashapplication.feature.account.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AccountModelMapperTest {

    @Test
    fun `toEntity - memetakan AccountModel ke AccountEntity dengan benar`() {
        val bcaProvider = ProviderCatalog.getById("bca")
        val model = AccountModel(
            id = "acc123",
            name = "Tabungan BCA",
            accountType = TypeAccount.BANK,
            providerAccount = bcaProvider,
            currentBalance = 1_500_000L,
            isActive = true,
            deletedAt = null,
        )

        val entity = model.toEntity()

        assertEquals("acc123", entity.id)
        assertEquals("Tabungan BCA", entity.name)
        assertEquals("bank", entity.accountType)
        assertEquals("bca", entity.providerId)
        assertEquals(1_500_000L, entity.currentBalance)
        assertTrue(entity.isActive)
        assertNull(entity.deletedAt)
        assertTrue(entity.lastUpdateAt > 0)
    }

    @Test
    fun `toEntity - menghasilkan UUID bila id null`() {
        val model = AccountModel(
            id = null,
            name = "Dompet Tunai",
            accountType = TypeAccount.CASH,
            currentBalance = 50_000L,
        )

        val entity = model.toEntity()

        assertNotNull(entity.id)
        assertTrue(entity.id.isNotBlank())
    }

    @Test
    fun `toDomain - memetakan AccountEntity ke AccountModel dengan provider dari katalog`() {
        val entity = AccountEntity(
            id = "acc456",
            name = "Gopay",
            accountType = "ewallet",
            providerId = "gopay",
            currentBalance = 250_000L,
            isActive = true,
            deletedAt = null,
            lastUpdateAt = 123456789L,
        )

        val domain = entity.toDomain()

        assertEquals("acc456", domain.id)
        assertEquals("Gopay", domain.name)
        assertEquals(TypeAccount.EWALLET, domain.accountType)
        assertEquals("gopay", domain.providerAccount?.id)
        assertEquals(250_000L, domain.currentBalance)
        assertTrue(domain.isActive)
        assertEquals(123456789L, domain.lastUpdateAt)
    }

    @Test
    fun `toDomain - provider null jika providerId tidak cocok atau null`() {
        val entity = AccountEntity(
            id = "acc789",
            name = "Cash Lain",
            accountType = "cash",
            providerId = null,
            currentBalance = 10_000L,
        )

        val domain = entity.toDomain()

        assertNull(domain.providerAccount)
        assertEquals(TypeAccount.CASH, domain.accountType)
    }
}
