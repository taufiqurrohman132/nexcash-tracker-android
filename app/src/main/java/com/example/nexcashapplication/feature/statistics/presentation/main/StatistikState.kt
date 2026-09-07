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
    val isModeRencanaActive: Boolean = false,
    val showMonthPicker: Boolean = false
//    val targetFinansial: List<TargetFinansialUi> = emptyList(),
//    val aiSummaryQuote: String? = null,
)

enum class BudgetStatus { AMAN, WASPADA, OVER }
enum class AlokasiMode { ALOKASI, REALISASI }


//data class TargetFinansialUi(
//    val id: Long,
//    val nama: String,
//    val progressPercent: Int,
//    val terkumpul: Long,
//    val target: Long,
//)