package com.example.nexcashapplication.feature.common.extentions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit test helper recent icons:
 * - [sortedIcon]: icon selalu naik ke depan meski sudah ada di daftar
 *   (regresi: cabang lama `contains -> this` membuat urutan tidak pernah
 *   berubah saat icon dipakai ulang), duplikat dibuang, daftar dibatasi limit
 * - [moveToFront]: duplikat dibuang dan hasil dibatasi maxSize
 */
class IconExtTest {

    private val a = icon("a")
    private val b = icon("b")
    private val c = icon("c")
    private val d = icon("d")

    @Test
    fun `sortedIcon - icon baru masuk di depan dan daftar dibatasi limit`() {
        val result = listOf(a, b, c).sortedIcon(d, limit = 3)

        // [d, a, b, c].take(3) -> c terbuang karena limit
        assertEquals(listOf(d, a, b), result)
    }

    @Test
    fun `sortedIcon - icon yang sudah ada tetap dipindah ke depan saat dipakai ulang`() {
        val result = listOf(a, b, c).sortedIcon(b, limit = 8)

        assertEquals(listOf(b, a, c), result)
    }

    @Test
    fun `sortedIcon - tidak membuat duplikat`() {
        val result = listOf(a, b).sortedIcon(a, limit = 8)

        assertEquals(listOf(a, b), result)
    }

    @Test
    fun `sortedIcon - daftar kosong icon tetap masuk`() {
        assertEquals(listOf(a), emptyList<CategoryIcon>().sortedIcon(a, limit = 8))
    }

    @Test
    fun `moveToFront - maxSize membatasi hasil gabungan`() {
        val result = listOf(a, b, c).moveToFront(d, maxSize = 3)

        assertEquals(d, result.first())
        assertEquals(3, result.size)
    }

    private fun icon(id: String) = CategoryIcon(id = id, icon = Icons.Default.Favorite)
}
