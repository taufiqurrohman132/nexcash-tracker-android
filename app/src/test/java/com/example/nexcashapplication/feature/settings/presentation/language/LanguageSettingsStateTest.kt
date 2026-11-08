package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LanguageSettingsStateTest {

    @Test
    fun `default values sesuai kontrak awal state`() {
        val state = LanguageSettingsState()

        assertFalse(state.isLoading)
        assertEquals("", state.searchQuery)
        assertTrue(state.listLanguage.isEmpty())
        assertEquals(AppLanguage.INDONESIA.code, state.selectedLanguageId)
        assertEquals(AppLanguage.INDONESIA.code, state.initialSelectedLanguageId)
        assertNull(state.error)
        assertFalse(state.hasChange)
    }

    @Test
    fun `hasChange bernilai false jika pilihan sama dengan initial`() {
        val state = LanguageSettingsState(
            selectedLanguageId = "en",
            initialSelectedLanguageId = "en",
        )
        assertFalse(state.hasChange)
    }

    @Test
    fun `hasChange bernilai true jika pilihan berbeda dengan initial`() {
        val state = LanguageSettingsState(
            selectedLanguageId = "en",
            initialSelectedLanguageId = "id",
        )
        assertTrue(state.hasChange)
    }

    @Test
    fun `copy state memperbarui data dengan benar`() {
        val original = LanguageSettingsState()
        val items = listOf(LanguageItemUiModel(id = "en", nameRes = R.string.settings_lang_en))
        val updated = original.copy(
            isLoading = true,
            searchQuery = "eng",
            listLanguage = items,
            selectedLanguageId = "en",
            initialSelectedLanguageId = "id",
        )

        assertTrue(updated.isLoading)
        assertEquals("eng", updated.searchQuery)
        assertEquals(items, updated.listLanguage)
        assertEquals("en", updated.selectedLanguageId)
        assertEquals("id", updated.initialSelectedLanguageId)
        assertTrue(updated.hasChange)
    }
}
