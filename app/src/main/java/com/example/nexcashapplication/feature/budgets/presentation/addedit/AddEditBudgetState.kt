package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate


data class AddEditBudgetState(
    val budgetId: String? = null,
    val categoryId: String? = null,
    val kategoriNama: String = "",
    val kategoriIkon: String? = null,

    val batasSebelumnya: Long = 0L,
    val batasBaruInput: String = "",
    val batasBaruParsed: Long = 0L,

    val showDateDialog: Boolean = false,
    val periodeTipe: RecurrenceType = RecurrenceType.MONTHLY,
    val tanggalMulai: LocalDate = LocalDate.now(),
    val tanggalBerakhir: LocalDate? = null,
    val isTanggalEditable: Boolean = false, // true hanya saat CUSTOM

    val isBerulang: Boolean = false,
    val isBerulangToggleEnabled: Boolean = true, // false saat MINGGUAN/CUSTOM

    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val isDirty: Boolean = false,
    val isFormValid: Boolean = false,
) {
    val hasChange: Boolean
        get() = batasBaruParsed != batasSebelumnya && batasBaruParsed > 0

    val previewTeksBerulang: String
        get() = "Sistem akan membuat anggaran $kategoriNama sebesar " +
                "Rp ${batasBaruParsed.formatCurrency()} secara otomatis pada tanggal 1 setiap bulannya."
}