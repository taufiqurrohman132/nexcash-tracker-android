package com.example.nexcashapplication.feature.category.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit test properti turunan [CategoryState]:
 * total/sisa anggaran mengikuti tab aktif, total pemasukan dari incomeList.
 */
class CategoryStateTest {

    private fun item(
        id: String,
        tipe: TypeCategory,
        terpakai: Long = 0L,
        budget: Long = 0L,
    ) = CategoryItem(
        id = id,
        nama = id,
        tipe = tipe,
        icon = CategoryIcons.fromId("location"),
        iconColor = Color.Black,
        terpakai = terpakai,
        budget = budget,
    )

    @Test
    fun `state kosong - semua total nol`() {
        val state = CategoryState()

        assertEquals(0L, state.totalAnggaran)
        assertEquals(0L, state.sisaAnggaran)
        assertEquals(0L, state.totalPemasukan)
    }

    @Test
    fun `tab EXPENSE - total dan sisa dihitung dari expenseList`() {
        val state = CategoryState(
            selectedTabCategory = TypeCategory.EXPENSE,
            expenseList = listOf(
                item("c1", TypeCategory.EXPENSE, terpakai = 300L, budget = 1_000L),
                item("c2", TypeCategory.EXPENSE, terpakai = 100L, budget = 500L),
            ),
            incomeList = listOf(
                item("c3", TypeCategory.INCOME, terpakai = 9_999L, budget = 8_888L),
            ),
        )

        assertEquals(1_500L, state.totalAnggaran)
        // sisa = (1000-300) + (500-100)
        assertEquals(1_100L, state.sisaAnggaran)
    }

    @Test
    fun `tab INCOME - total dan sisa berpindah ke incomeList`() {
        val state = CategoryState(
            selectedTabCategory = TypeCategory.INCOME,
            expenseList = listOf(
                item("c1", TypeCategory.EXPENSE, terpakai = 300L, budget = 1_000L),
            ),
            incomeList = listOf(
                item("c3", TypeCategory.INCOME, terpakai = 2_000L, budget = 5_000L),
            ),
        )

        assertEquals(5_000L, state.totalAnggaran)
        assertEquals(3_000L, state.sisaAnggaran)
    }

    @Test
    fun `totalPemasukan selalu dari incomeList terpakai - tidak peduli tab aktif`() {
        val state = CategoryState(
            selectedTabCategory = TypeCategory.EXPENSE, // tab sedang di pengeluaran
            incomeList = listOf(
                item("c3", TypeCategory.INCOME, terpakai = 7_500L, budget = 10_000L),
                item("c4", TypeCategory.INCOME, terpakai = 2_500L, budget = 4_000L),
            ),
        )

        assertEquals(10_000L, state.totalPemasukan)
    }
}
