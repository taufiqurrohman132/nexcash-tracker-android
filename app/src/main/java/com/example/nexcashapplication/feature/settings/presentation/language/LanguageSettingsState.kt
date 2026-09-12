package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel

data class LanguageSettingsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val listLanguage: List<LanguageItemUiModel> = emptyList(),
    val error: String? = null,
)