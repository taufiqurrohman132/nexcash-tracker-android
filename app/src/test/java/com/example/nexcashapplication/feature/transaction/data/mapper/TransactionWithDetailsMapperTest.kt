package com.example.nexcashapplication.feature.transaction.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionWithDetailsEntity
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class TransactionWithDetailsMapperTest {

    @Test
    fun `toDomain memetakan transaksi beserta kategori dan akun ketika keduanya ada`() {
        val txEntity = TransactionEntity(
            id = "tx-1",
            amount = 150_000L,
            title = "Makan Siang",
            categoryId = "cat-1",
            description = "Bersama teman",
            transactionDate = 1_700_000_000_000L,
            type = "expense",
            accountId = "acc-1",
            source = "manual",
        )
        val catEntity = CategoryEntity(
            id = "cat-1",
            name = "Makanan",
            type = "expense",
            icon = "food",
            color = "#FF0000",
        )
        val accEntity = AccountEntity(
            id = "acc-1",
            name = "BCA",
            accountType = "bank",
            currentBalance = 1_000_000L,
        )

        val entity = TransactionWithDetailsEntity(
            transaction = txEntity,
            category = catEntity,
            account = accEntity,
        )

        val domain = entity.toDomain()

        assertEquals("tx-1", domain.transaction.id)
        assertEquals(150_000L, domain.transaction.amount)
        assertEquals("Makan Siang", domain.transaction.title)
        assertEquals(TypeCategory.EXPENSE, domain.transaction.typeTransaction)

        assertNotNull(domain.category)
        assertEquals("cat-1", domain.category?.id)
        assertEquals("Makanan", domain.category?.name)
        assertEquals(TypeCategory.EXPENSE, domain.category?.type)

        assertNotNull(domain.account)
        assertEquals("acc-1", domain.account?.id)
        assertEquals("BCA", domain.account?.name)
        assertEquals(TypeAccount.BANK, domain.account?.accountType)
    }

    @Test
    fun `toDomain memetakan kategori dan akun bernilai null dengan benar`() {
        val txEntity = TransactionEntity(
            id = "tx-2",
            amount = 50_000L,
            title = "Bensin",
            categoryId = null,
            description = null,
            transactionDate = 1_700_000_000_000L,
            type = "expense",
            accountId = null,
            source = "manual",
        )

        val entity = TransactionWithDetailsEntity(
            transaction = txEntity,
            category = null,
            account = null,
        )

        val domain = entity.toDomain()

        assertEquals("tx-2", domain.transaction.id)
        assertNull(domain.category)
        assertNull(domain.account)
    }
}
