package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate
import java.time.temporal.ChronoUnit


data class AddEditBudgetState(
    val budgetId: String? = null,
    val categoryId: String? = null,
    val kategoriNama: String = "",
    val kategoriIkon: String? = null,

    val batasSebelumnya: Long = 0L,
    val batasBaruParsed: Long = 0L,

    val activeDateDialog: DateFieldTarget? = null,
    val periodeTipe: RecurrenceType = RecurrenceType.MONTHLY,
    val tanggalMulai: LocalDate = LocalDate.now(),
    val tanggalBerakhir: LocalDate = LocalDate.now().plusDays(1),
    val isTanggalEditable: Boolean = false, // true hanya saat CUSTOM

    // MONTHLY/WEEKLY selalu berulang → default true; switch hanya bisa di-toggle di CUSTOM.
    val isBerulang: Boolean = true,
    val isBerulangToggleEnabled: Boolean = false, // true hanya saat CUSTOM

    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val isDirty: Boolean = false,
    val isFormValid: Boolean = false,

    // Dialog konfirmasi hapus (hanya untuk budget yang sudah ada)
    val showDeleteConfirmation: Boolean = false,
) {
    val hasChange: Boolean
        get() = batasBaruParsed != batasSebelumnya && batasBaruParsed > 0

    /** Durasi periode dalam hari (inklusif). */
    val durasiHari: Long
        get() = ChronoUnit.DAYS.between(tanggalMulai, tanggalBerakhir) + 1

    val previewTeksBerulang: String
        get() {
            val nominal = "Rp ${batasBaruParsed.formatCurrency()}"
            val nama = kategoriNama.ifBlank { "kategori ini" }
            return when (periodeTipe) {
                RecurrenceType.MONTHLY ->
                    "Sistem akan membuat anggaran $nama sebesar $nominal " +
                            "secara otomatis pada tanggal 1 setiap bulannya."

                RecurrenceType.WEEKLY ->
                    "Sistem akan membuat anggaran $nama sebesar $nominal " +
                            "secara otomatis setiap awal minggu."

                RecurrenceType.CUSTOM ->
                    "Sistem akan membuat anggaran $nama sebesar $nominal " +
                            "secara otomatis setiap $durasiHari hari sejak " +
                            "${tanggalMulai}."
            }
        }
}

// di State
sealed interface DateFieldTarget {
    data object TanggalMulai : DateFieldTarget
    data object TanggalAkhir : DateFieldTarget
}
