package com.example.nexcashapplication.feature.statistics.presentation.main

import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import java.time.YearMonth

data class StatistikState(
    val isLoading: Boolean = false,
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
)

enum class BudgetStatus { AMAN, WASPADA, OVER }
enum class AlokasiMode { ALOKASI, REALISASI }

/**
 * Rincian tab "Realisasi" pada kartu Alokasi Dana: donut pengeluaran atau
 * pemasukan per kategori. Tidak dipakai saat mode [AlokasiMode.ALOKASI]
 * (budget hanya ada di kategori pengeluaran).
 */
enum class RealisasiTipe { PENGELUARAN, PENDAPATAN }
