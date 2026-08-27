package com.example.nexcashapplication.feature.statistics.presentation.main

data class StatistikState(
    val isLoading: Boolean = true,
    val selectedMonth: Int = 10,
    val selectedYear: Int = 2023,

    // Ringkasan atas
    val totalPengeluaran: Long = 0,
    val totalPengeluaranTrendPercent: Float = 0f, // +12% -> naik = buruk utk pengeluaran
    val sisaAnggaran: Long = 0,
    val sisaAnggaranStatus: AnggaranStatus = AnggaranStatus.AMAN,
    val tingkatTabungan: Float = 0f, // 25.5%
    val tingkatTabunganTrendPercent: Float = 0f, // +2.1%

    // Alokasi Dana
    val activeAlokasiTab: AlokasiTab = AlokasiTab.ALOKASI,
    val totalAlokasi: Long = 0,
    val alokasiKategori: List<AlokasiKategoriUi> = emptyList(),

    // Insight Pintar (rule-based, bukan AI)
    val insightBaruCount: Int = 0,
    val insights: List<InsightUi> = emptyList(),

    // Target Finansial
    val targetFinansial: List<TargetFinansialUi> = emptyList(),

    val error: String? = null,
)

enum class AnggaranStatus { AMAN, WASPADA, OVER }
enum class AlokasiTab { ALOKASI, REALISASI }

data class AlokasiKategoriUi(
    val kategoriId: Long,
    val nama: String,
    val persentase: Float,
    val nominal: Long,
    val colorHex: String, // domain tetap String, no Compose dependency
)

data class InsightUi(
    val id: String,
    val judul: String,
    val deskripsi: String,
    val iconType: InsightIconType,
)

enum class InsightIconType { KOPI, MAKAN, BELANJA, LAINNYA }

data class TargetFinansialUi(
    val id: Long,
    val nama: String,
    val progressPercent: Int,
    val terkumpul: Long,
    val target: Long,
)