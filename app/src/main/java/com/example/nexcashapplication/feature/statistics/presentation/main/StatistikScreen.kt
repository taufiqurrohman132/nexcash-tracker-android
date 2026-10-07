package com.example.nexcashapplication.feature.statistics.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.component.MonthNavigator
import com.example.nexcashapplication.core.component.MonthPicker
import com.example.nexcashapplication.core.component.donutchart.DonutSlice
import com.example.nexcashapplication.core.component.donutchart.InteractiveDonutChart
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.extension.formatDecimal2
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextTertiary
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatistikScreen(
    navController: NavHostController,
    viewModel: StatistikViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StatistikEffect.NavigateToUbahAlokasi ->
                    navController.navigate(Route.BudgetRoute.EditMultiple)
            }
        }
    }

    StatistikContent(
        state = state,
        onEvent = viewModel::onEvent,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatistikContent(
    state: StatistikState,
    onEvent: (StatistikEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.stat_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                actions = {
                    MonthNavigator(
                        modifier = Modifier.padding(end = 16.dp),
                        month = state.selectedMonth,
                        onMonthSelected = { onEvent(StatistikEvent.OnMonthSelected(it)) },
                        onOpenPicker = { onEvent(StatistikEvent.OnGantiBulanClick) },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading && state.alokasiDana.isEmpty()) {
                // Frame pertama tanpa cache (atau belum pernah ada data):
                // area kosong dulu, bukan kartu Rp 0 + donut kosong.
                // Kalau masih ada data lama, kartu lama tetap tampil supaya
                // tidak berkedip saat ganti bulan/tab.
                Spacer(
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp),
                )
            } else {
                TotalPengeluaranCard(state.totalPengeluaran, state.pengeluaranTrendPercent)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryStatCard(
                        title = stringResource(R.string.stat_remaining_budget),
                        value = state.sisaAnggaran.formatCompact(),
                        badge = stringResource(state.statusAnggaran.labelResId),
                        icon = Icons.Outlined.ShoppingBag,
                        modifier = Modifier.weight(1f),
                    )
                    SummaryStatCard(
                        title = stringResource(R.string.stat_savings_rate_title),
                        value = String.format(
                            Locale.getDefault(),
                            "%.1f%%",
                            state.tingkatTabungan,
                        ),
                        badge = String.format(
                            Locale.getDefault(),
                            "%+.1f%%",
                            state.tabunganTrendPercent,
                        ),
                        icon = Icons.Outlined.TrendingUp,
                        modifier = Modifier.weight(1f),
                    )
                }

                AlokasiDanaCard(
                    total = state.totalAlokasi,
                    items = state.alokasiDana,
                    mode = state.alokasiMode,
                    realisasiTipe = state.realisasiTipe,
                    onTabChange = { onEvent(StatistikEvent.OnAlokasiTabChange(it)) },
                    onTipeChange = { onEvent(StatistikEvent.OnRealisasiTipeChange(it)) },
                )
            }

            TindakanCepatCard(onClick = { onEvent(StatistikEvent.OnUbahAlokasiClick) })

            MonthPicker(
                visible = state.showMonthPicker,
                currentMonth = state.selectedMonth.monthValue - 1,
                currentYear = state.selectedMonth.year,
                confirmButtonCLicked = { month, year ->
                    onEvent(StatistikEvent.OnMonthSelected(YearMonth.of(year, month + 1)))
                },
                cancelClicked = {
                    onEvent(StatistikEvent.OnMonthPickerDismiss)
                },
            )
        }
    }
}


@Preview(showBackground = true, name = "Navigasi bulan - di bulan berjalan")
@Composable
private fun MonthNavigatorPreview() {
    NexCashApplicationTheme {
        MonthNavigator(
            month = YearMonth.now(),
            onMonthSelected = {},
            onOpenPicker = {},
        )
    }
}

@Composable
fun AlokasiDanaCard(
    total: Long,
    items: List<AlokasiKategoriUi>,
    mode: AlokasiMode,
    realisasiTipe: RealisasiTipe,
    onTabChange: (AlokasiMode) -> Unit,
    onTipeChange: (RealisasiTipe) -> Unit,
) {
    Column(
        Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = PrimaryCyan,
                    spotColor = PrimaryCyan,
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(stringResource(R.string.stat_allocation_title), style = MaterialTheme.typography.titleMedium)
                val basis = when {
                    mode == AlokasiMode.ALOKASI -> stringResource(R.string.stat_basis_target)
                    realisasiTipe == RealisasiTipe.PENGELUARAN ->
                        stringResource(R.string.stat_basis_actual_expense)

                    else -> stringResource(R.string.stat_basis_actual_income)
                }
                Text(
                    text = stringResource(
                        R.string.stat_basis_value_format,
                        basis,
                        total.formatCurrency(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        SegmentedTabRow(
            selected = mode,
            options = listOf(
                AlokasiMode.ALOKASI to stringResource(R.string.stat_tab_allocation),
                AlokasiMode.REALISASI to stringResource(R.string.stat_tab_actual),
            ),
            onSelect = onTabChange,
        )

        // Tab sekunder hanya relevan di Realisasi — budget (Alokasi) memang
        // cuma ada di kategori pengeluaran.
        if (mode == AlokasiMode.REALISASI) {
            Spacer(Modifier.height(8.dp))

            val tipeOptions = listOf(
                RealisasiTipe.PENGELUARAN to stringResource(R.string.stat_tipe_expense),
                RealisasiTipe.PENDAPATAN to stringResource(R.string.stat_tipe_income),
            )
            SecondaryTabRow(
                selectedTabIndex = tipeOptions.indexOfFirst { it.first == realisasiTipe },
                modifier = Modifier.fillMaxWidth(),
                // tanpa garis pemisah bawah — kartu sudah punya paddingnya sendiri
                divider = {},
            ) {
                tipeOptions.forEach { (value, label) ->
                    Tab(
                        selected = realisasiTipe == value,
                        onClick = { onTipeChange(value) },
                        text = { Text(label) },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Memastikan slices diperbarui setiap kali data items berubah
        val slices = remember(items, mode, realisasiTipe) {
            items.mapIndexed { index, item ->
                DonutSlice(
                    label = item.nama,
                    value = item.persentase,
                    color = item.colorHex.toColor()
                )
            }
        }


        InteractiveDonutChart(
            slices = slices,
            modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(220.dp),
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // chunked tiap recomposition = alokasi list baru; ingat hasilnya
            val rows = remember(items) { items.chunked(2) }
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { item ->
                        // weight(1f) → tiap sel dapat (lebar - jarak) / 2 → kolom seimbang
                        LegendItem(item, Modifier.weight(1f))
                    }
                }
            }
        }

    }
}

@Composable
private fun LegendItem(item: AlokasiKategoriUi, modifier: Modifier = Modifier) {
    // parse warna hex tiap recomposition = buang-buang; warna ikut item saja
    val dotColor = remember(item.colorHex) { item.colorHex.toColor() }
    Row(
        modifier = modifier,verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                    .background(dotColor, CircleShape)
                    .padding(horizontal = 4.dp)
        ){
            Text(item.persentase.formatDecimal2(), style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.width(6.dp))
        Column {
            // 1 baris + ellipsis: nama panjang jangan bikin sel jadi 2 baris
            Text(
                text = item.nama,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.nominal.formatCurrency(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


/**
 * Segmented control generik dipakai dua kali di [AlokasiDanaCard]:
 * tab utama `AlokasiMode` dan tab sekunder `RealisasiTipe`.
 */
@Composable
fun <T> SegmentedTabRow(
    selected: T,
    options: List<Pair<T, String>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
    ) {
        options.forEach { (value, label) ->
            val isSelected = selected == value
            Text(
                text = label,
                textAlign = TextAlign.Center,
                modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onSelect(value) }
                        .padding(vertical = 8.dp),
            )
        }
    }
}


@Composable
fun TindakanCepatCard(onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.stat_quick_actions), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.stat_help),
                color = PrimaryCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            onClick = onClick,
            color = PrimaryCyan.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryCyan),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.stat_edit_allocation),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = stringResource(R.string.stat_edit_allocation_desc),
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = PrimaryCyan,
                )
            }
        }
    }
}

@Composable
fun SummaryStatCard(
    title: String,
    value: String,
    badge: String,
    icon: ImageVector,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                Text(badge, color = PrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TotalPengeluaranCard(totalPengeluaran: Long, deltaPercent: Float) {
    Surface(
        color = PrimaryCyan.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = PrimaryCyan,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // tren pengeluaran: naik = merah, turun = hijau, datar = abu
                    val trendColor = when {
                        deltaPercent > 0f -> Color(0xFFD32F2F)
                        deltaPercent < 0f -> Color(0xFF2E7D32)
                        else -> Color.Gray
                    }
                    Icon(
                        if (deltaPercent >= 0f) {
                            Icons.AutoMirrored.Filled.TrendingUp
                        } else {
                            Icons.AutoMirrored.Filled.TrendingDown
                        },
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        String.format(Locale.getDefault(), "%+.1f%%", deltaPercent),
                        color = trendColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.stat_total_expense), fontSize = 13.sp, color = Color.Gray)
            Text(totalPengeluaran.formatCurrency(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private val dummyListAlokasi = listOf(
    AlokasiKategoriUi(
        kategoriId = "1",
        nama = "Makanan",
        persentase = 35f,
        nominal = 1_400_000,
        colorHex = "#FF6B6B",
    ),
    AlokasiKategoriUi(
        kategoriId = "2",
        nama = "Transportasi",
        persentase = 20f,
        nominal = 800_000,
        colorHex = "#4ECDC4",
    ),
    AlokasiKategoriUi(
        kategoriId = "3",
        nama = "Hiburan",
        persentase = 15f,
        nominal = 600_000,
        colorHex = "#FFD93D",
    ),
    AlokasiKategoriUi(
        kategoriId = "4",
        nama = "Tabungan",
        persentase = 30f,
        nominal = 1_200_000,
        colorHex = "#6BCB77",
    ),
)

@Preview(showBackground = true, name = "Statistik - Aman")
@Composable
fun StatistikContentPreview() {
    NexCashApplicationTheme {
        StatistikContent(
            state = StatistikState(
                isLoading = false,
                selectedMonth = YearMonth.now(),
                totalPengeluaran = 2_450_000,
                pengeluaranTrendPercent = -12.5f,
                sisaAnggaran = 1_550_000,
                statusAnggaran = BudgetStatus.AMAN,
                tingkatTabungan = 38.5f,
                tabunganTrendPercent = 5.2f,
                alokasiMode = AlokasiMode.ALOKASI,
                totalAlokasi = 4_000_000,
                alokasiDana = dummyListAlokasi,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Statistik - Over Budget")
@Composable
fun StatistikContentOverPreview() {
    NexCashApplicationTheme {
        StatistikContent(
            state = StatistikState(
                totalPengeluaran = 5_200_000,
                pengeluaranTrendPercent = 22.0f,
                sisaAnggaran = -1_200_000,
                statusAnggaran = BudgetStatus.OVER,
                tingkatTabungan = 8.0f,
                tabunganTrendPercent = -15.0f,
                alokasiMode = AlokasiMode.REALISASI,
                realisasiTipe = RealisasiTipe.PENGELUARAN,
                totalAlokasi = 4_000_000,
                alokasiDana = listOf(
                    AlokasiKategoriUi("1", "Makanan", 50f, 2_600_000, "#FF6B6B"),
                    AlokasiKategoriUi("2", "Transportasi", 25f, 1_300_000, "#4ECDC4"),
                    AlokasiKategoriUi("3", "Hiburan", 25f, 1_300_000, "#FFD93D"),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Statistik - Loading")
@Composable
fun StatistikContentLoadingPreview() {
    NexCashApplicationTheme {
        StatistikContent(
            state = StatistikState(isLoading = true),
            onEvent = {},
        )
    }
}