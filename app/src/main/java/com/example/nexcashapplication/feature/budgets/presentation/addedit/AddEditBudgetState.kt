package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.core.extension.formatCurrency
import java.time.LocalDate


enum class PeriodeTipe { BULANAN, MINGGUAN, CUSTOM }

data class AddEditBudgetState(
    val budgetId: Long = 0L,
    val kategoriNama: String = "",
    val kategoriIkon: String? = null,

    val batasSebelumnya: Long = 0L,
    val batasBaruInput: String = "",
    val batasBaruParsed: Long = 0L,

    val periodeTipe: PeriodeTipe = PeriodeTipe.BULANAN,
    val tanggalMulai: LocalDate? = null,
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