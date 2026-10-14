package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel

data class LanguageSettingsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val listLanguage: List<LanguageItemUiModel> = emptyList(),
    val selectedLanguageId: String = AppLanguage.INDONESIA.code,
    val initialSelectedLanguageId: String = AppLanguage.INDONESIA.code,
    /** Gagal memuat daftar bahasa; null = tidak ada error. */
    val error: UiText? = null,
){
    val hasChange: Boolean = selectedLanguageId != initialSelectedLanguageId
}