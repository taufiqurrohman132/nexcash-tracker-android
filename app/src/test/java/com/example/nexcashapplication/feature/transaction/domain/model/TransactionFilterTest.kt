package com.example.nexcashapplication.feature.transaction.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit test pemetaan argumen route `All(categoryId, accountId)` → [TransactionFilter]
 * via [TransactionFilter.fromRoute] (ADR-002).
 *
 * Route dibatasi argumen primitif karena inferensi NavType Navigation tidak menutup
 * tipe sealed; seluruh keputusan filter ada di pemetaan ini sehingga bisa diuji di JVM
 * tanpa `SavedStateHandle.toRoute()`.
 */
class TransactionFilterTest {

    @Test
    fun `hanya categoryId terisi → ByCategory`() {
        assertEquals(
            TransactionFilter.ByCategory("cat-1"),
            TransactionFilter.fromRoute(categoryId = "cat-1", accountId = null),
        )
    }

    @Test
    fun `hanya accountId terisi → ByAccount`() {
        assertEquals(
            TransactionFilter.ByAccount("acc-1"),
            TransactionFilter.fromRoute(categoryId = null, accountId = "acc-1"),
        )
    }

    @Test
    fun `keduanya null → None tanpa filter`() {
        assertEquals(
            TransactionFilter.None,
            TransactionFilter.fromRoute(categoryId = null, accountId = null),
        )
    }

    @Test
    fun `keduanya terisi → ByCategory menang sebagai guard`() {
        assertEquals(
            TransactionFilter.ByCategory("cat-1"),
            TransactionFilter.fromRoute(categoryId = "cat-1", accountId = "acc-1"),
        )
    }
}
