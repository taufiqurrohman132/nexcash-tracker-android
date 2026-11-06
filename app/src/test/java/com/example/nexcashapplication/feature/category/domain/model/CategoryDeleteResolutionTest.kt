package com.example.nexcashapplication.feature.category.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CategoryDeleteResolutionTest {

    @Test
    fun `memverifikasi tipe resolusi delete kategori`() {
        val deleteAll = CategoryDeleteResolution.DeleteAllTransactions
        val uncategorize = CategoryDeleteResolution.Uncategorize
        val moveTo = CategoryDeleteResolution.MoveToCategory(targetCategoryId = "cat-99")

        assertTrue(deleteAll is CategoryDeleteResolution)
        assertTrue(uncategorize is CategoryDeleteResolution)
        assertTrue(moveTo is CategoryDeleteResolution)
        assertEquals("cat-99", moveTo.targetCategoryId)
    }

    @Test
    fun `categoryName model menyimpan properti dengan benar`() {
        val catName = CategoryName(
            id = "c1",
            name = "Makanan",
            icon = "food_icon",
            color = "#FF0000",
        )

        assertEquals("c1", catName.id)
        assertEquals("Makanan", catName.name)
        assertEquals("food_icon", catName.icon)
        assertEquals("#FF0000", catName.color)
    }

    @Test
    fun `weeklyTrend model menyimpan list data dan label`() {
        val trend = WeeklyTrend(
            data = listOf(10f, 20f, 30f),
            labels = listOf("Sen", "Sel", "Rab"),
        )

        assertEquals(3, trend.data.size)
        assertEquals(listOf("Sen", "Sel", "Rab"), trend.labels)
    }
}
