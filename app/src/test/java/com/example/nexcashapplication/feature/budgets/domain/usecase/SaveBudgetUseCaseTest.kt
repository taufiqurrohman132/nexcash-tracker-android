package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.getWeeklyPeriodByLocale
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SaveBudgetUseCaseTest {

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

    private fun request(
        amount: Long = 2_000L,
        type: RecurrenceType = RecurrenceType.MONTHLY,
        budgetId: String? = null,
        categoryId: String? = "cat_1",
        customStart: LocalDate = today,
        customEnd: LocalDate = today.plusDays(1),
        isRecurring: Boolean = true,
    ) = SaveBudgetUseCase.Request(
        budgetId = budgetId,
        categoryId = categoryId,
        amount = amount,
        periodType = type,
        customStart = customStart,
        customEnd = customEnd,
        isRecurringRequested = isRecurring,
        today = today,
    )

    private suspend fun save(
        repo: FakeBudgetRepository,
        amount: Long = 2_000L,
        type: RecurrenceType = RecurrenceType.MONTHLY,
        budgetId: String? = null,
        categoryId: String? = "cat_1",
        customStart: LocalDate = today,
        customEnd: LocalDate = today.plusDays(1),
        isRecurring: Boolean = true,
    ): SaveBudgetUseCase.Result =
        SaveBudgetUseCase(repo, GetBudgetPeriodUseCase())(
            request(amount, type, budgetId, categoryId, customStart, customEnd, isRecurring),
        )

    // ── Validasi ──────────────────────────────────────────────────────────

    @Test
    fun `amount 0 atau negatif ditolak`() = runBlocking {
        val repo = FakeBudgetRepository()

        val result = save(repo, amount = 0L)

        assertInstanceOf(SaveBudgetUseCase.Result.Failure::class.java, result)
        assertTrue(repo.stored.isEmpty())
    }

    @Test
    fun `custom dengan end sebelum start ditolak`() = runBlocking {
        val repo = FakeBudgetRepository()

        val result = save(
            repo,
            type = RecurrenceType.CUSTOM,
            customStart = today,
            customEnd = today, // sama → tidak setelah start
        )

        assertInstanceOf(SaveBudgetUseCase.Result.Failure::class.java, result)
        assertTrue(repo.stored.isEmpty())
    }

    @Test
    fun `categoryId null ditolak`() = runBlocking {
        val repo = FakeBudgetRepository()

        val result = save(repo, categoryId = null)

        assertInstanceOf(SaveBudgetUseCase.Result.Failure::class.java, result)
        assertTrue(repo.stored.isEmpty())
    }

    // ── Create ────────────────────────────────────────────────────────────

    @Test
    fun `tanpa anggaran aktif - membuat root baru dengan window periode berjalan`() = runBlocking {
        val repo = FakeBudgetRepository()

        val result = save(repo, amount = 1_500L)

        assertEquals(SaveBudgetUseCase.Result.Success, result)
        val created = repo.stored.single()
        assertTrue(created.isSeriesRoot) // self-reference
        assertEquals(LocalDate.of(2026, 9, 1), created.startDate)
        assertEquals(LocalDate.of(2026, 9, 30), created.endDate)
        assertEquals(1_500L, created.amount)
        assertTrue(created.isRecurring)
    }

    // ── Update (tipe sama) ────────────────────────────────────────────────

    @Test
    fun `tipe sama - update instance aktif dan periode mendatang, riwayat tidak berubah`() =
        runBlocking {
            val repo = FakeBudgetRepository()
            repo.seed(
                row("r1", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)),
                row(
                    "s1",
                    LocalDate.of(2026, 9, 1),
                    LocalDate.of(2026, 9, 30),
                    groupId = "r1",
                ),
                row(
                    "o1",
                    LocalDate.of(2026, 10, 1),
                    LocalDate.of(2026, 10, 31),
                    groupId = "r1",
                ),
            )

            val result = save(repo, amount = 2_000L, budgetId = "s1")

            assertEquals(SaveBudgetUseCase.Result.Success, result)
            assertEquals(3, repo.stored.size) // tidak ada baris baru
            assertEquals(1_000L, repo.stored.first { it.id == "r1" }.amount) // riwayat aman
            assertEquals(2_000L, repo.stored.first { it.id == "s1" }.amount) // instance aktif
            assertEquals(2_000L, repo.stored.first { it.id == "o1" }.amount) // periode mendatang
            // window tersimpan tidak berubah
            assertEquals(LocalDate.of(2026, 9, 1), repo.stored.first { it.id == "s1" }.startDate)
            assertEquals(LocalDate.of(2026, 9, 30), repo.stored.first { it.id == "s1" }.endDate)
        }

    // ── Ganti tipe periode ────────────────────────────────────────────────

    @Test
    fun `ganti tipe - tutup seri lama lalu buat root baru`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            row("p1", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)), // riwayat
            row("r1", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)), // root aktif
            row("c1", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), groupId = "r1"),
        )
        val weeklyStart = getWeeklyPeriodByLocale(today).first

        val result = save(repo, amount = 3_000L, type = RecurrenceType.WEEKLY)

        assertEquals(SaveBudgetUseCase.Result.Success, result)

        // Baris mulai >= window baru dihapus; riwayat (end < windowStart) tetap
        assertFalse(repo.stored.any { it.id == "c1" })
        assertTrue(repo.stored.any { it.id == "p1" })
        assertEquals(3, repo.stored.size) // p1 + r1 (terpotong) + root baru

        // Root lama dipotong ke windowStart-1 dan generasinya dimatikan
        val oldRoot = repo.stored.first { it.id == "r1" }
        assertEquals(weeklyStart.minusDays(1), oldRoot.endDate)
        assertFalse(oldRoot.isRecurring)

        // Root baru weekly dengan window yang mencakup hari ini
        val newRoot = repo.stored.single { it.recurrenceType == RecurrenceType.WEEKLY }
        assertTrue(newRoot.isSeriesRoot)
        assertEquals(3_000L, newRoot.amount)
        assertTrue(!newRoot.startDate.isAfter(today) && !newRoot.endDate.isBefore(today))
    }

    @Test
    fun `ganti tipe - anak dihapus sebelum root (urutan FK RESTRICT)`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            row("r1", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 27)),
            row("c1", LocalDate.of(2026, 9, 28), LocalDate.of(2026, 10, 4), groupId = "r1"),
        )

        val result = save(
            repo,
            amount = 4_000L,
            type = RecurrenceType.CUSTOM,
            customStart = LocalDate.of(2026, 9, 20),
            customEnd = LocalDate.of(2026, 10, 10),
            isRecurring = false,
        )

        assertEquals(SaveBudgetUseCase.Result.Success, result)
        // Seri lama habis (keduanya start >= window baru), root baru CUSTOM yang tersisa
        assertEquals(listOf("c1", "r1"), repo.deletedIds)
        assertEquals(1, repo.stored.size)
        val created = repo.stored.single()
        assertTrue(created.isSeriesRoot)
        assertEquals(RecurrenceType.CUSTOM, created.recurrenceType)
    }

    // ── Custom one-shot ───────────────────────────────────────────────────

    @Test
    fun `one-shot custom - root dibuat sesuai window pilihan user`() = runBlocking {
        val repo = FakeBudgetRepository()

        val result = save(
            repo,
            amount = 750_000L,
            type = RecurrenceType.CUSTOM,
            customStart = LocalDate.of(2026, 9, 20),
            customEnd = LocalDate.of(2026, 9, 26),
            isRecurring = false,
        )

        assertEquals(SaveBudgetUseCase.Result.Success, result)
        val created = repo.stored.single()
        assertEquals(RecurrenceType.CUSTOM, created.recurrenceType)
        assertFalse(created.isRecurring)
        assertEquals(LocalDate.of(2026, 9, 20), created.startDate)
        assertEquals(LocalDate.of(2026, 9, 26), created.endDate)
    }
}
