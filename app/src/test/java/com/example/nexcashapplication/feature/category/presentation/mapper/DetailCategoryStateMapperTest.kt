package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.common.extentions.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DetailCategoryStateMapperTest {

    @Test
    fun `toUiModel memetakan Category ke CategoryUiItemModel dengan budget dan usedBudget`() {
        val category = Category(
            id = "cat-1",
            parentId = "parent-1",
            name = "Makan Siang",
            type = TypeCategory.EXPENSE,
            color = "#AABBCC",
            iconId = "restaurant",
        )

        val uiModel = category.toUiModel(totalBudget = 500_000L, usedBudget = 120_000L)

        assertEquals("cat-1", uiModel.id)
        assertEquals("parent-1", uiModel.parentId)
        assertEquals("Makan Siang", uiModel.name)
        assertEquals(TypeCategory.EXPENSE, uiModel.typeCategory)
        assertEquals("#AABBCC".toColor(), uiModel.color)
        assertEquals(500_000L, uiModel.totalBudget)
        assertEquals(120_000L, uiModel.usedBudget)
    }

    @Test
    fun `toUiModel dengan parameter default menggunakan totalBudget 0 dan usedBudget 0`() {
        val category = Category(
            id = "cat-2",
            name = "Gaji",
            type = TypeCategory.INCOME,
            color = "#FFFFFF",
            iconId = "money",
        )

        val uiModel = category.toUiModel()

        assertEquals(0L, uiModel.totalBudget)
        assertEquals(0L, uiModel.usedBudget)
    }
}
