package com.example.nexcashapplication.feature.settings.presentation.language.model

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.AppLanguage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageItemUiModelTest {

    @Test
    fun `nameResId memetakan setiap AppLanguage ke string resource yang benar`() {
        assertEquals(R.string.settings_lang_id, AppLanguage.INDONESIA.nameResId)
        assertEquals(R.string.settings_lang_en, AppLanguage.ENGLISH.nameResId)
        assertEquals(R.string.settings_lang_fr, AppLanguage.FRENCH.nameResId)
        assertEquals(R.string.settings_lang_de, AppLanguage.GERMAN.nameResId)
        assertEquals(R.string.settings_lang_es, AppLanguage.SPANISH.nameResId)
        assertEquals(R.string.settings_lang_pt, AppLanguage.PORTUGUESE.nameResId)
        assertEquals(R.string.settings_lang_ru, AppLanguage.RUSSIAN.nameResId)
        assertEquals(R.string.settings_lang_zh, AppLanguage.CHINESE.nameResId)
        assertEquals(R.string.settings_lang_ja, AppLanguage.JAPANESE.nameResId)
    }

    @Test
    fun `semua nilai enum AppLanguage memiliki pemetaan nameResId`() {
        AppLanguage.entries.forEach { lang ->
            // Memastikan tidak ada yang melempar exception atau bernilai 0
            val resId = lang.nameResId
            org.junit.jupiter.api.Assertions.assertNotEquals(0, resId)
        }
    }

    @Test
    fun `LanguageItemUiModel properti tersimpan dengan benar`() {
        val model = LanguageItemUiModel(id = "id", nameRes = R.string.settings_lang_id)
        assertEquals("id", model.id)
        assertEquals(R.string.settings_lang_id, model.nameRes)
    }
}
