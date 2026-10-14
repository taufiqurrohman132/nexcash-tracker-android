package com.example.nexcashapplication.feature.settings.presentation.main

import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface SettingsEffect {
    data class NavigateTo(val destination: SettingsDestinations) : SettingsEffect
//    data object NavigateToEditProfile : SettingsEffect
//    data object ShowLogoutConfirmDialog : SettingsEffect
//    data object ShowDeleteAccountConfirmDialog : SettingsEffect
//    data object NavigateToLogin : SettingsEffect       // setelah logout beneran
//    data object NavigateToOnboarding : SettingsEffect  // setelah akun kehapus
    data class ShowSnackbar(val message: UiText) : SettingsEffect
}