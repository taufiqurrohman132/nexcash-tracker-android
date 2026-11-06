package com.example.nexcashapplication.feature.category.presentation.mapper

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.AddCategoryState
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.common.extentions.toHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class AddCategoryStateMapperTest {

    @Test
    fun `toDomain memetakan AddCategoryState ke Category domain model dengan benar`() {
        val selectedIcon = CategoryIcons.fromId("car")
        val state = AddCategoryState(
            id = "cat-1",
            name = "Transport",
            typeCategory = TypeCategory.EXPENSE,
            selectedColor = Color(0xFFFF5722),
            selectedIcon = selectedIcon,
            note = "Catatan bensin",
        )

        val domain = state.toDomain(id = "cat-1", parentId = "parent-9")

        assertEquals("cat-1", domain.id)
        assertEquals("parent-9", domain.parentId)
        assertEquals("Transport", domain.name)
        assertEquals(TypeCategory.EXPENSE, domain.type)
        assertEquals("car", domain.iconId)
        assertEquals(Color(0xFFFF5722).toHex(), domain.color)
        assertEquals("Catatan bensin", domain.note)
    }

    @Test
    fun `toDomain dengan selectedIcon null mengisi iconId kosong`() {
        val state = AddCategoryState(
            name = "Tanpa Icon",
            selectedIcon = null,
        )

        val domain = state.toDomain(id = "c-empty")

        assertEquals("", domain.iconId)
    }

    @Test
    fun `toUiState memetakan Category domain model ke AddCategoryState`() {
        val category = Category(
            id = "cat-10",
            parentId = "parent-1",
            name = "Kopi",
            type = TypeCategory.EXPENSE,
            color = "#00FF00",
            iconId = "coffee",
            note = "Kopi pagi",
        )
        val recentColors = listOf(Color.Red, Color.Blue)
        val recentIcons = listOf(CategoryIcons.fromId("coffee"))

        val uiState = category.toUiState(
            recentColors = recentColors,
            recentIcons = recentIcons,
        )

        assertEquals("cat-10", uiState.id)
        assertEquals("Kopi", uiState.name)
        assertEquals(TypeCategory.EXPENSE, uiState.typeCategory)
        assertEquals("coffee", uiState.selectedIcon?.id)
        assertEquals("#00FF00".toColor(), uiState.selectedColor)
        assertEquals("Kopi pagi", uiState.note)
        assertEquals(recentColors, uiState.recentColors)
        assertEquals(recentIcons, uiState.recentIcons)
    }
}
