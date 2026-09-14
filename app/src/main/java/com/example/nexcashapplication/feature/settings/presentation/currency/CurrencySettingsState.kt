package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel

data class CurrencySettingsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val listLanguage: List<LanguageItemUiModel> = emptyList(),
    val selectedLanguageId: String = AppLanguage.INDONESIA.code,
    val initialSelectedLanguageId: String = AppLanguage.INDONESIA.code,
    val error: String? = null,
) {
    val hasChange: Boolean = selectedLanguageId != initialSelectedLanguageId
}