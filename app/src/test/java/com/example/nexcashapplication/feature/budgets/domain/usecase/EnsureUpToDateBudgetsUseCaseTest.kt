package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EnsureUpToDateBudgetsUseCaseTest {

    private val today: LocalDate = LocalDate.of(2026, 9, 24)

    private fun root(
        id: String = "root_1",
        start: LocalDate,
        end: LocalDate,
        amount: Long = 5_000L,
        type: RecurrenceType = RecurrenceType.MONTHLY,
        isRecurring: Boolean = true,
    ) = Budget(
        id = id,
        categoryId = "cat_1",
        startDate = start,
        endDate = end,
        amount = amount,
        recurrenceType = type,
        isRecurring = isRecurring,
    )

    @Test
    fun `monthly - generate periode sampai hari ini tercakup, lalu berhenti`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(root(start = LocalDate.of(2026, 7, 1), end = LocalDate.of(2026, 7, 31)))

        EnsureUpToDateBudgetsUseCase(repo)(today)

        // Juli (root) + Agustus + September
        assertEquals(3, repo.stored.size)
        val agustus = repo.stored.first { it.startDate == LocalDate.of(2026, 8, 1) }
        assertEquals(LocalDate.of(2026, 8, 31), agustus.endDate)
        val september = repo.stored.first { it.startDate == LocalDate.of(2026, 9, 1) }
        assertEquals(LocalDate.of(2026, 9, 30), september.endDate)

        // Start periode berikutnya selalu end sebelumnya + 1 hari (tanpa gap/tumpang tindih)
        assertEquals(
            LocalDate.of(2026, 7, 31).plusDays(1),
            agustus.startDate,
        )
        assertEquals(agustus.endDate.plusDays(1), september.startDate)

        // Anak seri menunjuk ke root (self-reference), amount disalin dari root
        repo.stored.filter { it.id != "root_1" }.forEach {
            assertEquals("root_1", it.recurringGroupId)
            assertEquals(5_000L, it.amount)
            assertTrue(it.isRecurring)
        }

        // Idempoten: run ulang tidak menambah baris
        EnsureUpToDateBudgetsUseCase(repo)(today)
        assertEquals(3, repo.stored.size)
    }

    @Test
    fun `edit amount di tengah periode diwarisi periode berikutnya`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            root(start = LocalDate.of(2026, 7, 1), end = LocalDate.of(2026, 7, 31), amount = 5_000L),
            root(
                id = "child_1",
                start = LocalDate.of(2026, 8, 1),
                end = LocalDate.of(2026, 8, 31),
                amount = 9_000L, // instance Agustus yang sudah diedit user
            ).copy(recurringGroupId = "root_1"),
        )

        EnsureUpToDateBudgetsUseCase(repo)(today)

        val september = repo.stored.first { it.startDate == LocalDate.of(2026, 9, 1) }
        assertEquals(9_000L, september.amount)
        // Agustus tidak diduplikasi, tapi September tetap digenerate
        assertEquals(1, repo.stored.count { it.startDate == LocalDate.of(2026, 8, 1) })
        assertEquals(3, repo.stored.size)
    }

    @Test
    fun `safety cap - seri sangat basi dibatasi 24 periode per run`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(root(start = LocalDate.of(2020, 1, 1), end = LocalDate.of(2020, 1, 31)))

        EnsureUpToDateBudgetsUseCase(repo)(today)

        // root + 24 periode hasil generate
        assertEquals(25, repo.stored.size)
        // Masih belum mencakup hari ini → run berikutnya melanjutkan
        assertTrue(repo.stored.maxOf { it.endDate }.isBefore(today))
    }

    @Test
    fun `custom - interval tetap sesuai durasi root`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            root(
                start = LocalDate.of(2026, 9, 1),
                end = LocalDate.of(2026, 9, 10),
                type = RecurrenceType.CUSTOM,
            ),
        )

        EnsureUpToDateBudgetsUseCase(repo)(today)

        val generated = repo.stored.filter { it.id != "root_1" }.sortedBy { it.startDate }
        assertEquals(2, generated.size)
        // Interval = durasi root (9 hari), dihitung dari end - start
        val interval = ChronoUnit.DAYS.between(
            LocalDate.of(2026, 9, 1),
            LocalDate.of(2026, 9, 10),
        )
        generated.forEach { row ->
            assertEquals(interval, ChronoUnit.DAYS.between(row.startDate, row.endDate))
        }
        assertEquals(LocalDate.of(2026, 9, 11), generated[0].startDate)
        assertEquals(LocalDate.of(2026, 9, 21), generated[1].startDate)
        assertTrue(!generated.last().endDate.isBefore(today)) // run berhenti setelah tercakup
    }

    @Test
    fun `weekly - periode berikutnya start = end sebelumnya + 1 hari`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            root(
                start = LocalDate.of(2026, 8, 31),
                end = LocalDate.of(2026, 9, 6),
                type = RecurrenceType.WEEKLY,
            ),
        )

        EnsureUpToDateBudgetsUseCase(repo)(today)

        val next = repo.stored.first { it.id != "root_1" }
        assertEquals(LocalDate.of(2026, 9, 7), next.startDate)
        assertEquals(RecurrenceType.WEEKLY, next.recurrenceType)
        // Periode berikutnya dimulai tepat setelah end sebelumnya dan berdurasi 7 hari
        assertEquals(6L, ChronoUnit.DAYS.between(next.startDate, next.endDate))
        // Sampai run berhenti, seri sudah mencakup hari ini
        assertTrue(!repo.stored.maxOf { it.endDate }.isBefore(today))
    }

    @Test
    fun `one-shot (is_recurring false) tidak digenerate`() = runBlocking {
        val repo = FakeBudgetRepository()
        repo.seed(
            root(
                start = LocalDate.of(2026, 7, 1),
                end = LocalDate.of(2026, 7, 31),
                isRecurring = false,
            ),
        )

        EnsureUpToDateBudgetsUseCase(repo)(today)

        assertEquals(1, repo.stored.size)
        assertFalse(repo.stored.single().isRecurring)
    }
}
