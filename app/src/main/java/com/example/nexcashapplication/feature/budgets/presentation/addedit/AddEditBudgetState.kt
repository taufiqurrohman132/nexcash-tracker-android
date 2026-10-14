package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
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
    /** Pesan error validasi/persistensi; null = tidak ada error. */
    val errorMessage: UiText? = null,

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

    /**
     * Pratinjau anggaran berulang, masih berupa [UiText] (resolve di composable
     * dengan `.asString()`).
     *
     * @param fallbackNamaKategori nama kategori pengganti bila [kategoriNama] kosong —
     *   sudah berupa teks ter-resolve dari UI (`stringResource`) karena kelas ini
     *   tidak membawa Context.
     */
    fun previewTeksBerulang(fallbackNamaKategori: String): UiText {
        val nominal = batasBaruParsed.formatCurrency()
        val nama = kategoriNama.ifBlank { fallbackNamaKategori }
        return when (periodeTipe) {
            RecurrenceType.MONTHLY ->
                uiText(R.string.budget_recurring_preview_monthly, nama, nominal)

            RecurrenceType.WEEKLY ->
                uiText(R.string.budget_recurring_preview_weekly, nama, nominal)

            RecurrenceType.CUSTOM ->
                uiText(
                    R.string.budget_recurring_preview_custom,
                    nama,
                    nominal,
                    durasiHari,
                    tanggalMulai,
                )
        }
    }
}

// di State
sealed interface DateFieldTarget {
    data object TanggalMulai : DateFieldTarget
    data object TanggalAkhir : DateFieldTarget
}
