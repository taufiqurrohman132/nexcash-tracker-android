package com.example.nexcashapplication.feature.settings.presentation.language.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.AppLanguage

data class LanguageItemUiModel(
    val id: String,

    /** Nama tampil bahasa (dalam bahasanya sendiri) — kunci di `values/strings_settings.xml`. */
    @StringRes val nameRes: Int,
)

/**
 * Nama tampil tiap [AppLanguage].
 *
 * UI layer (peta UI boleh bergantung ke resource, pola `TypeAccountUiMapper`); dipakai daftar
 * bahasa maupun baris ringkasan Bahasa di layar Pengaturan.
 */
@get:StringRes
val AppLanguage.nameResId: Int
    get() = when (this) {
        AppLanguage.INDONESIA -> R.string.settings_lang_id
        AppLanguage.ENGLISH -> R.string.settings_lang_en
        AppLanguage.FRENCH -> R.string.settings_lang_fr
        AppLanguage.GERMAN -> R.string.settings_lang_de
        AppLanguage.SPANISH -> R.string.settings_lang_es
        AppLanguage.PORTUGUESE -> R.string.settings_lang_pt
        AppLanguage.RUSSIAN -> R.string.settings_lang_ru
        AppLanguage.CHINESE -> R.string.settings_lang_zh
        AppLanguage.JAPANESE -> R.string.settings_lang_ja
    }
