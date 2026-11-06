package com.example.nexcashapplication.feature.category.presentation.addnew

import com.example.nexcashapplication.core.domain.model.TypeCategory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddCategoryStateTest {

    @Test
    fun `isValid bernilai true hanya jika name tidak kosong atau blank`() {
        val stateBlank = AddCategoryState(name = "   ")
        assertFalse(stateBlank.isValid)

        val stateEmpty = AddCategoryState(name = "")
        assertFalse(stateEmpty.isValid)

        val stateValid = AddCategoryState(name = "Transportasi")
        assertTrue(stateValid.isValid)
    }

    @Test
    fun `default state memiliki nilai awal yang tepat`() {
        val state = AddCategoryState()

        assertEquals("", state.id)
        assertEquals("", state.name)
        assertEquals(TypeCategory.EXPENSE, state.typeCategory)
        assertEquals("", state.note)
        assertFalse(state.showColorPalletDialog)
        assertTrue(state.recentColors.isEmpty())
        assertTrue(state.recentIcons.isEmpty())
    }
}
