package com.example.nexcashapplication.feature.category.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Unit test [CategoryItem.persentase] (dipakai progress bar di list kategori). */
class CategoryItemTest {

    private fun item(terpakai: Long, budget: Long) = CategoryItem(
        id = "c1",
        nama = "Makanan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcons.fromId("location"),
        iconColor = Color.Black,
        terpakai = terpakai,
        budget = budget,
    )

    @Test
    fun `persentase normal - dibulatkan ke bawah`() {
        assertEquals(25, item(terpakai = 50_000L, budget = 200_000L).persentase)
        assertEquals(33, item(terpakai = 1L, budget = 3L).persentase)
    }

    @Test
    fun `budget 0 - persentase 0 (tanpa pembagian nol)`() {
        assertEquals(0, item(terpakai = 500L, budget = 0L).persentase)
        assertEquals(0, item(terpakai = 0L, budget = 0L).persentase)
    }

    @Test
    fun `terpakai melebihi budget - persentase lebih dari 100`() {
        assertEquals(150, item(terpakai = 300L, budget = 200L).persentase)
    }
}
