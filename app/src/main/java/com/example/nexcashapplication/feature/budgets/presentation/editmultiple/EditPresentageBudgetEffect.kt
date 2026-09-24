package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

/**
 * Efek satu-kali dari [EditPresentageBudgetViewModel] ke layar.
 * Dipakai sekali lalu selesai (navigasi/snackbar), bukan bagian dari state.
 */
sealed interface EditPresentageBudgetEffect {

    /** Kembali ke layar sebelumnya. */
    data object NavigateBack : EditPresentageBudgetEffect

    /** Buka form tambah kategori baru (Route.CategoryRoute.AddNew). */
    data object NavigateToAddCategory : EditPresentageBudgetEffect

    /** Tampilkan pesan singkat: error validasi atau hasil penyimpanan. */
    data class ShowSnackbar(val message: String) : EditPresentageBudgetEffect
}
