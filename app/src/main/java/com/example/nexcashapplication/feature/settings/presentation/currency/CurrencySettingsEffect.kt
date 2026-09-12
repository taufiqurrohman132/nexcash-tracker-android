package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface CurrencySettingsEffect {
    data class NavigateTo(val destination: SettingsDestinations) : CurrencySettingsEffect
//    data object NavigateToEditProfile : SettingsEffect
//    data object ShowLogoutConfirmDialog : SettingsEffect
//    data object ShowDeleteAccountConfirmDialog : SettingsEffect
//    data object NavigateToLogin : SettingsEffect       // setelah logout beneran
//    data object NavigateToOnboarding : SettingsEffect  // setelah akun kehapus
    data class ShowSnackbar(val message: String) : CurrencySettingsEffect
}