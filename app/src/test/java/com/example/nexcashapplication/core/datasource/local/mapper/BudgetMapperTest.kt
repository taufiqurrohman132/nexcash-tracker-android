package com.example.nexcashapplication.core.datasource.local.mapper

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BudgetMapperTest {

    private fun newRoot(id: String? = null, groupId: String? = null) = Budget(
        id = id,
        categoryId = "cat_1",
        startDate = LocalDate.of(2026, 9, 1),
        endDate = LocalDate.of(2026, 9, 30),
        amount = 1_000_000L,
        recurrenceType = RecurrenceType.MONTHLY,
        isRecurring = true,
        recurringGroupId = groupId,
    )

    @Test
    fun `root baru mendapat self-reference ke id-nya sendiri`() {
        val entity = newRoot(id = null).toEntity()

        assertEquals(entity.id, entity.recurringGroupId)
        assertTrue(entity.recurringGroupId != null)
    }

    @Test
    fun `update mempertahankan id dan recurring group yang tersimpan`() {
        val root = newRoot(id = "root_1", groupId = null).toEntity()
        val child = newRoot(id = "child_1", groupId = "root_1").toEntity()

        assertEquals("root_1", root.id)
        assertEquals("root_1", root.recurringGroupId)
        assertEquals("child_1", child.id)
        assertEquals("root_1", child.recurringGroupId)
    }

    @Test
    fun `roundtrip entity ke domain mempertahankan recurringGroupId`() {
        val budget = newRoot(id = "root_1", groupId = "root_1")

        assertEquals(budget, budget.toEntity().toDomain())
    }
}
