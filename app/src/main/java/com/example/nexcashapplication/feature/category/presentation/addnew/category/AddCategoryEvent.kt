package com.example.nexcashapplication.feature.category.presentation.addnew.category

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.category.model.BudgetPeriod

sealed class AddCategoryEvent {
    // Input Teks
    data class OnNameChanged(val name: String) : AddCategoryEvent()
    data class OnBudgetChanged(val amount: Long?) : AddCategoryEvent()
    data class OnNotesChanged(val notes: String) : AddCategoryEvent()

    // Pilihan Dropdown & Opsi
    data class OnTypeSelected(val type: TypeCategory) : AddCategoryEvent() // Pengeluaran/Pemasukan
    data class OnParentCategorySelected(val parentId: String?) : AddCategoryEvent() // Opsional
    data class OnPeriodSelected(val period: BudgetPeriod) : AddCategoryEvent()
    data class OnAccountSelected(val accountId: String) : AddCategoryEvent()

    // Pemilihan Visual
    data class OnColorSelected(val color: Color) : AddCategoryEvent()
    data class OnChangeIcon(val icon: CategoryIcon) : AddCategoryEvent() // Tombol "Ubah"

    // Aksi Navigasi & Tombol Utama
    object OnSaveClicked : AddCategoryEvent() // Tombol "Simpan" & "Selesaikan Pembuatan"
    object OnCancelClicked : AddCategoryEvent() // Tombol "Batal & Kembali"
    object OnBackClicked : AddCategoryEvent() // Tombol "<"
}