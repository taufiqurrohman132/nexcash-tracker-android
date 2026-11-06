package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.common.extentions.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeleteCategoryStateMapperTest {

    @Test
    fun `toDeleteUiModel memetakan CategoryName ke CategoryDeleteItemUI`() {
        val model = CategoryName(
            id = "c1",
            name = "Tagihan",
            icon = "bill",
            color = "#ABCDEF",
        )

        val ui = model.toDeleteUiModel()

        assertEquals("c1", ui.id)
        assertEquals("Tagihan", ui.name)
        assertEquals("#ABCDEF".toColor(), ui.iconColor)
    }

    @Test
    fun `toCategoryDeleteItemList memetakan list CategoryName ke list CategoryDeleteItemUI`() {
        val list = listOf(
            CategoryName(id = "c1", name = "A", icon = "i1", color = "#111111"),
            CategoryName(id = "c2", name = "B", icon = "i2", color = "#222222"),
        )

        val result = list.toCategoryDeleteItemList()

        assertEquals(2, result.size)
        assertEquals("c1", result[0].id)
        assertEquals("A", result[0].name)
        assertEquals("c2", result[1].id)
        assertEquals("B", result[1].name)
    }
}
