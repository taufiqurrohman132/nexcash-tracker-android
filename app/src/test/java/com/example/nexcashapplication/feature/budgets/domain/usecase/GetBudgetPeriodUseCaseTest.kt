package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.common.getWeeklyPeriodByLocale
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GetBudgetPeriodUseCaseTest {

    private val useCase = GetBudgetPeriodUseCase()

    @Test
    fun `periode WEEKLY - mengembalikan rentang minggu sesuai locale`() {
        val date = LocalDate.of(2026, 11, 5) // Kamis
        val expected = getWeeklyPeriodByLocale(date)

        val result = useCase(
            isRecurring = true,
            type = RecurrenceType.WEEKLY,
            startDate = date,
            endDate = null,
        )

        assertEquals(expected, result)
        assertEquals(6, result.first.until(result.second).days)
    }

    @Test
    fun `periode MONTHLY - mengembalikan tanggal 1 hingga akhir bulan`() {
        val date = LocalDate.of(2026, 11, 15)
        val expected = Pair(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30))

        val result = useCase(
            isRecurring = true,
            type = RecurrenceType.MONTHLY,
            startDate = date,
            endDate = null,
        )

        assertEquals(expected, result)
        assertEquals(getMonthlyPeriod(date), result)
    }

    @Test
    fun `periode CUSTOM dengan endDate - mengembalikan tanggal kustom pilihan user`() {
        val start = LocalDate.of(2026, 11, 10)
        val end = LocalDate.of(2026, 11, 25)

        val result = useCase(
            isRecurring = false,
            type = RecurrenceType.CUSTOM,
            startDate = start,
            endDate = end,
        )

        assertEquals(Pair(start, end), result)
    }

    @Test
    fun `periode CUSTOM tanpa endDate - melempar IllegalStateException`() {
        val start = LocalDate.of(2026, 11, 10)

        val exception = assertThrows(IllegalStateException::class.java) {
            useCase(
                isRecurring = false,
                type = RecurrenceType.CUSTOM,
                startDate = start,
                endDate = null,
            )
        }

        assertEquals("CUSTOM budget must have currentPeriodEnd", exception.message)
    }
}
