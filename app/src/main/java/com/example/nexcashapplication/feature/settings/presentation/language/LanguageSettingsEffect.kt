package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface LanguageSettingsEffect {
    data class NavigateTo(val destination: SettingsDestinations) : LanguageSettingsEffect
    data object NavigateBack : LanguageSettingsEffect

    /**
     * Bahasa sudah tersimpan ke DataStore dan [com.example.nexcashapplication.core.i18n.AppLocale]
     * sudah diterapkan. Layar tinggal menutup diri dan me-recreate Activity supaya resources ikut.
     */
    data class Saved(val languageCode: String) : LanguageSettingsEffect
//    data object NavigateToEditProfile : SettingsEffect
//    data object ShowLogoutConfirmDialog : SettingsEffect
//    data object ShowDeleteAccountConfirmDialog : SettingsEffect
//    data object NavigateToLogin : SettingsEffect       // setelah logout beneran
//    data object NavigateToOnboarding : SettingsEffect  // setelah akun kehapus
    data class ShowSnackbar(val message: UiText) : LanguageSettingsEffect
}