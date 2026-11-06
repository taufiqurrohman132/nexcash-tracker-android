package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.common.extentions.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CategoryStateMapperTest {

    @Test
    fun `toCategoryItem memetakan Category ke CategoryItem dengan nilai budget dan usedBudget`() {
        val category = Category(
            id = "cat-1",
            name = "Makanan",
            type = TypeCategory.EXPENSE,
            color = "#FF1122",
            iconId = "food",
        )

        val item = category.toCategoryItem(usedBudget = 250_000L, budget = 1_000_000L)

        assertEquals("cat-1", item.id)
        assertEquals("Makanan", item.nama)
        assertEquals(TypeCategory.EXPENSE, item.tipe)
        assertEquals("#FF1122".toColor(), item.iconColor)
        assertEquals(250_000L, item.terpakai)
        assertEquals(1_000_000L, item.budget)
    }

    @Test
    fun `toCategoryItemList memetakan list Category ke list CategoryItem dengan default 0`() {
        val list = listOf(
            Category(id = "c1", name = "A", type = TypeCategory.EXPENSE, color = "#111111", iconId = "icon1"),
            Category(id = "c2", name = "B", type = TypeCategory.INCOME, color = "#222222", iconId = "icon2"),
        )

        val result = list.toCategoryItemList()

        assertEquals(2, result.size)
        assertEquals("c1", result[0].id)
        assertEquals(0L, result[0].terpakai)
        assertEquals(0L, result[0].budget)
        assertEquals("c2", result[1].id)
        assertEquals(TypeCategory.INCOME, result[1].tipe)
    }

    @Test
    fun `toCategoryNameItemUiModel memetakan id dan nameCategory`() {
        val category = Category(
            id = "c-99",
            name = "Investasi Saham",
            type = TypeCategory.EXPENSE,
            color = "#123456",
            iconId = "chart",
        )

        val result = category.toCategoryNameItemUiModel()

        assertEquals("c-99", result.id)
        assertEquals("Investasi Saham", result.nameCategory)
    }
}
