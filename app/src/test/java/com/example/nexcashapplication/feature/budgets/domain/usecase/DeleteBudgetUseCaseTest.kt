package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DeleteBudgetUseCaseTest {

    private val today: LocalDate = LocalDate.of(2026, 9, 24)

    private fun row(
        id: String,
        start: LocalDate,
        end: LocalDate,
        amount: Long = 1_000L,
        type: RecurrenceType = RecurrenceType.MONTHLY,
        groupId: String? = null,
        isRecurring: Boolean = true,
    ) = Budget(
        id = id,
        categoryId = "cat_1",
        startDate = start,
        endDate = end,
        amount = amount,
        recurrenceType = type,
        isRecurring = isRecurring,
        recurringGroupId = groupId,
    )

    private suspend fun delete(
        repo: FakeBudgetRepository,
        targetId: String,
    ): DeleteBudgetUseCase.Result {
        val target = repo.stored.first { it.id == targetId }
        return DeleteBudgetUseCase(repo)(target, today = today)
    }

    // ── Tutup seri: aktif + mendatang terhapus, riwayat aman ─────────────

    @Test
    fun `seri aktif - root dan anak terhapus, anak sebelum root demi FK RESTRICT`() = runBlocking {
        val repo = FakeBudgetRepository()
        // Root sedang berjalan (Sep) + satu periode mendatang (Okt)
        val root = row("r1", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30))
        val child = row(
            "c1",
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 31),
            groupId = "r1",
        )
        repo.seed(root, child)

        val result = delete(repo, targetId = "r1")

        assertInstanceOf(DeleteBudgetUseCase.Result.Success::class.java, result)
        // Tidak ada baris tersisa → lazy generation tidak punya root untuk dilanjut
        assertTrue(repo.stored.isEmpty())
        // Anak (start terbesar) dihapus lebih dulu sebelum root
        assertEquals(listOf("c1", "r1"), repo.deletedIds)
    }

    @Test
    fun `riwayat lampau tetap tersimpan dan generasi periode dihentikan`() = runBlocking {
        val repo = FakeBudgetRepository()
        val root = row("r1", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), amount = 900L)
        val active = row(
            "c1",
            LocalDate.of(2026, 9, 1),
            LocalDate.of(2026, 9, 30),
            groupId = "r1",
        )
        val future = row(
            "c2",
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 31),
            groupId = "r1",
        )
        repo.seed(root, active, future)

        val result = delete(repo, targetId = "c1")

        assertInstanceOf(DeleteBudgetUseCase.Result.Success::class.java, result)
        // Riwayat Agustus tetap ada dengan nilai aslinya
        assertEquals(listOf("r1"), repo.stored.map { it.id })
        assertEquals(900L, repo.stored.first().amount)
        // Root tersimpan tapi dimatikan → EnsureUpToDateBudgets tidak bikin periode baru
        assertFalse(repo.stored.first().isRecurring)
        // Periode berjalan + mendatang terhapus (anak sebelum root)
        assertEquals(listOf("c2", "c1"), repo.deletedIds)
    }

    @Test
    fun `one-shot aktif terhapus seluruhnya`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            row(
                "s1",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 10, 10),
                type = RecurrenceType.CUSTOM,
                isRecurring = false,
            ),
        )

        val result = delete(repo, targetId = "s1")

        assertInstanceOf(DeleteBudgetUseCase.Result.Success::class.java, result)
        assertTrue(repo.stored.isEmpty())
        assertEquals(listOf("s1"), repo.deletedIds)
    }

    // ── Isolasi antar seri ───────────────────────────────────────────────

    @Test
    fun `seri lain pada kategori yang sama tidak tersentuh`() = runBlocking {
        val repo = FakeBudgetRepository()
        // Seri A: root lampau + periode aktif
        repo.seed(row("a_root", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)))
        repo.seed(
            row(
                "a_active",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                groupId = "a_root",
            ),
        )
        // Seri B: root self-referencing yang sedang berjalan (entitas terpisah)
        repo.seed(row("b_root", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))

        delete(repo, targetId = "a_active")

        // Seri B utuh
        assertTrue(repo.stored.any { it.id == "b_root" })
        assertFalse(repo.deletedIds.contains("b_root"))
        // Riwayat seri A tetap ada, hanya yang aktif yang hilang
        assertTrue(repo.stored.any { it.id == "a_root" })
        assertFalse(repo.stored.any { it.id == "a_active" })
    }

    // ── Validasi ─────────────────────────────────────────────────────────

    @Test
    fun `target tanpa id dan group ditolak`() = runBlocking {
        val repo = FakeBudgetRepository()
        val invalid = Budget(
            id = null,
            categoryId = "cat_1",
            startDate = LocalDate.of(2026, 9, 1),
            endDate = LocalDate.of(2026, 9, 30),
            amount = 1_000L,
            recurrenceType = RecurrenceType.MONTHLY,
            isRecurring = true,
            recurringGroupId = null,
        )

        val result = DeleteBudgetUseCase(repo)(invalid, today = today)

        assertInstanceOf(DeleteBudgetUseCase.Result.Failure::class.java, result)
        assertTrue(repo.deletedIds.isEmpty())
    }
}
