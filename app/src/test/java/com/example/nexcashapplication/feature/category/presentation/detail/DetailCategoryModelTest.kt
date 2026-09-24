package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.ui.UiState
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.detail.model.DetailCategoryUiModel
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit test model & mapper halaman detail kategori:
 * - default [DetailCategoryState] dan [DetailCategoryUiModel]
 * - properti turunan [CategoryUiItemModel] (usedPercentage, remainingBudget)
 * - mapper [Category.toUiModel] (field, ikon, budget default)
 *
 * Warna sengaja tidak diuji: `String.toColor()` bergantung pada android.graphics
 * yang di-mock di JVM unit test.
 */
class DetailCategoryModelTest {

    // ── Default state/model ──────────────────────────────────────────────

    @Test
    fun `state awal - Loading dengan flag load-more default`() {
        val state = DetailCategoryState()

        assertTrue(state.dataState is UiState.Loading)
        assertFalse(state.isTransactionsLoadingMore)
        assertTrue(state.canLoadMoreTransactions)
    }

    @Test
    fun `ui model default - semua daftar kosong`() {
        val ui = DetailCategoryUiModel(category = null)

        assertTrue(ui.subCategories.isEmpty())
        assertTrue(ui.weeklyData.isEmpty())
        assertTrue(ui.weeklyLabels.isEmpty())
        assertTrue(ui.recentTransactions.isEmpty())
    }

    // ── Mapper Category.toUiModel ────────────────────────────────────────

    @Test
    fun `toUiModel - field terpetakan dan budget default nol`() {
        val ui = category(id = "c1-sub", parentId = "c1", name = "Makan Siang").toUiModel()

        assertEquals("c1-sub", ui.id)
        assertEquals("c1", ui.parentId)
        assertEquals("Makan Siang", ui.name)
        assertEquals(TypeCategory.EXPENSE, ui.typeCategory)
        assertEquals(0L, ui.totalBudget)
        assertEquals(0L, ui.usedBudget)
    }

    @Test
    fun `toUiModel - ikon sesuai iconId dan fallback ke location bila tidak dikenal`() {
        assertEquals(
            "pet",
            category(id = "c1", iconId = "pet").toUiModel().icon.id,
        )
        assertEquals(
            CategoryIcons.fromId("location").id,
            category(id = "c1", iconId = "tidak-ada").toUiModel().icon.id,
        )
    }

    // ── Properti turunan CategoryUiItemModel ─────────────────────────────

    @Test
    fun `usedPercentage - dibulatkan ke bawah`() {
        assertEquals(30, uiModel(total = 1_000L, used = 300L).usedPercentage)
        assertEquals(33, uiModel(total = 3L, used = 1L).usedPercentage)
    }

    @Test
    fun `usedPercentage - budget 0 tidak memicu pembagian nol`() {
        assertEquals(0, uiModel(total = 0L, used = 500L).usedPercentage)
        assertEquals(0, uiModel(total = 0L, used = 0L).usedPercentage)
    }

    @Test
    fun `usedPercentage - terpakai melebihi budget tetap dihitung`() {
        assertEquals(150, uiModel(total = 200L, used = 300L).usedPercentage)
    }

    @Test
    fun `remainingBudget - total dikurangi terpakai, bisa negatif`() {
        assertEquals(700L, uiModel(total = 1_000L, used = 300L).remainingBudget)
        assertEquals(-100L, uiModel(total = 200L, used = 300L).remainingBudget)
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun category(
        id: String,
        parentId: String? = null,
        name: String = "Makanan",
        iconId: String = "pet",
    ) = Category(
        id = id,
        parentId = parentId,
        name = name,
        type = TypeCategory.EXPENSE,
        iconId = iconId,
        color = "#FF0000",
    )

    private fun uiModel(total: Long, used: Long) = CategoryUiItemModel(
        id = "c1",
        name = "Makanan",
        icon = CategoryIcons.fromId("location"),
        color = Color.Black,
        totalBudget = total,
        usedBudget = used,
        typeCategory = TypeCategory.EXPENSE,
    )
}
