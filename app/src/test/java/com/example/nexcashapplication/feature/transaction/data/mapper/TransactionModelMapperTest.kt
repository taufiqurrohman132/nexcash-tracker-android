package com.example.nexcashapplication.feature.transaction.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class TransactionModelMapperTest {

    @Test
    fun `toDomain memetakan semua field TransactionEntity ke domain Transaction`() {
        val entity = TransactionEntity(
            id = "tx-123",
            amount = 75_000L,
            title = "Makan Siang",
            categoryId = "cat-1",
            description = "Nasi Padang",
            transactionDate = 1_700_000_000_000L,
            type = "expense",
            accountId = "acc-1",
            source = "manual",
        )

        val domain = entity.toDomain()

        assertEquals("tx-123", domain.id)
        assertEquals(75_000L, domain.amount)
        assertEquals("Makan Siang", domain.title)
        assertEquals("cat-1", domain.categoryId)
        assertEquals("Nasi Padang", domain.description)
        assertEquals(Instant.fromEpochMilliseconds(1_700_000_000_000L), domain.dateTime)
        assertEquals(TypeCategory.EXPENSE, domain.typeTransaction)
        assertEquals("acc-1", domain.accountId)
    }

    @Test
    fun `toDomain memetakan tipe income dengan benar`() {
        val entity = TransactionEntity(
            id = "tx-456",
            amount = 5_000_000L,
            title = "Gaji",
            categoryId = "cat-2",
            description = null,
            transactionDate = 1_700_000_000_000L,
            type = "income",
            accountId = "acc-2",
            source = "manual",
        )

        val domain = entity.toDomain()

        assertEquals(TypeCategory.INCOME, domain.typeTransaction)
        assertEquals(null, domain.description)
    }

    @Test
    fun `toEntity memetakan domain Transaction dengan id yang sudah ada`() {
        val domain = Transaction(
            id = "tx-789",
            amount = 120_000L,
            title = "Belanja Bulanan",
            categoryId = "cat-3",
            description = "Sabun dan sikat gigi",
            dateTime = Instant.fromEpochMilliseconds(1_700_123_456_789L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-3",
        )

        val entity = domain.toEntity()

        assertEquals("tx-789", entity.id)
        assertEquals(120_000L, entity.amount)
        assertEquals("Belanja Bulanan", entity.title)
        assertEquals("cat-3", entity.categoryId)
        assertEquals("Sabun dan sikat gigi", entity.description)
        assertEquals(1_700_123_456_789L, entity.transactionDate)
        assertEquals("expense", entity.type)
        assertEquals("acc-3", entity.accountId)
        assertEquals("", entity.source)
    }

    @Test
    fun `toEntity saat id null menghasilkan id baru berupa UUID`() {
        val domain = Transaction(
            id = null,
            amount = 25_000L,
            title = "Kopi",
            categoryId = "cat-4",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_000_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-4",
        )

        val entity = domain.toEntity()

        assertNotNull(entity.id)
        assertTrue(entity.id.isNotBlank())
        assertEquals("expense", entity.type)
    }
}
