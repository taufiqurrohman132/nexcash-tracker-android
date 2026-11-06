package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.mapper

import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TypeAccountMapperTest {

    @Test
    fun `toLabelRes - semua TypeAccount memiliki string resource yang valid`() {
        for (type in TypeAccount.entries) {
            val resId = type.toLabelRes()
            assertTrue(resId != 0, "Resource ID untuk ${type.name} tidak boleh 0")
        }
    }

    @Test
    fun `getAllTypeAccountItems - memuat seluruh varian TypeAccount`() {
        val items = getAllTypeAccountItems()

        assertEquals(TypeAccount.entries.size, items.size)
        val mappedTypes = items.map { it.type }.toSet()
        assertEquals(TypeAccount.entries.toSet(), mappedTypes)
    }

    @Test
    fun `toUiItem - memetakan ikon dan warna untuk setiap tipe`() {
        for (type in TypeAccount.entries) {
            val item = type.toUiItem()
            assertEquals(type, item.type)
            assertNotNull(item.icon)
            assertNotNull(item.iconColor)
        }
    }
}
