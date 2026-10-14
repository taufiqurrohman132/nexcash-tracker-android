package com.example.nexcashapplication.feature.statistics.presentation.main

import androidx.annotation.StringRes
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import java.time.YearMonth

data class StatistikState(
    // true sampai emisi pertama — kalau data kosong, layar menampilkan
    // indikator, bukan kartu Rp 0 + donut kosong
    val isLoading: Boolean = true,
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalPengeluaran: Long = 0,
    val pengeluaranTrendPercent: Float = 0f,
    val sisaAnggaran: Long = 0,
    val statusAnggaran: BudgetStatus = BudgetStatus.AMAN,
    val tingkatTabungan: Float = 0f,
    val tabunganTrendPercent: Float = 0f,
    val alokasiDana: List<AlokasiKategoriUi> = emptyList(),
    val totalAlokasi: Long = 0,
    val alokasiMode: AlokasiMode = AlokasiMode.ALOKASI,
    val realisasiTipe: RealisasiTipe = RealisasiTipe.PENGELUARAN,
    val showMonthPicker: Boolean = false,
    /** Gagal memuat statistik; null = tidak ada error. */
    val error: UiText? = null,
)

/**
 * Status kesehatan anggaran. [labelResId] dipakai di UI (`stringResource(...)`)
 * supaya badge tidak memakai `.name` mentah yang tidak ikut terjemahan.
 */
enum class BudgetStatus(@StringRes val labelResId: Int) {
    AMAN(R.string.stat_status_safe),
    WASPADA(R.string.stat_status_warning),
    OVER(R.string.stat_status_over),
}
enum class AlokasiMode { ALOKASI, REALISASI }

/**
 * Rincian tab "Realisasi" pada kartu Alokasi Dana: donut pengeluaran atau
 * pemasukan per kategori. Tidak dipakai saat mode [AlokasiMode.ALOKASI]
 * (budget hanya ada di kategori pengeluaran).
 */
enum class RealisasiTipe { PENGELUARAN, PENDAPATAN }
