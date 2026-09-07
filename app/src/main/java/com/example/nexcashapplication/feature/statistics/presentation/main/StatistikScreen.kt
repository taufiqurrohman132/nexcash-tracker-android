package com.example.nexcashapplication.feature.statistics.presentation.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.component.MonthPicker
import com.example.nexcashapplication.core.component.donutchart.DonutSlice
import com.example.nexcashapplication.core.component.donutchart.InteractiveDonutChart
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import java.time.YearMonth


@Composable
fun StatistikScreen(
    navController: NavHostController,
    viewModel: StatistikViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StatistikEffect.NavigateToUbahAlokasi -> {

                }

                is StatistikEffect.NavigateToTargetList -> {

                }

                is StatistikEffect.NavigateToTargetDetail -> {

                }

                is StatistikEffect.ShowMonthPicker -> Unit // TODO: tampilkan dialog
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
                        text = "Statistic",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconCircular(
                            icon = Icons.Default.KeyboardArrowLeft,
                            iconColor = Color.Black,
                            backgroundColor = BorderGray,
                            iconToCountainerRatio = 1,
                            onClick = {
                                onEvent(
                                    StatistikEvent.OnMonthSelected(
                                        state.selectedMonth.minusMonths(
                                            1,
                                        ),
                                    ),
                                )
                            },
                        )
                        OutlinedButton(
                            onClick = { onEvent(StatistikEvent.OnGantiBulanClick) },
                        ) {
                            Text("${state.selectedMonth.month} ${state.selectedMonth.year}")
                        }
                        IconCircular(
                            icon = Icons.Default.KeyboardArrowRight,
                            iconColor = Color.Black,
                            backgroundColor = BorderGray,
                            iconToCountainerRatio = 1,
                            onClick = {
                                onEvent(
                                    StatistikEvent.OnMonthSelected(
                                        state.selectedMonth.plusMonths(
                                            1,
                                        ),
                                    ),
                                )
                            },
                        )
                    }
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
            TotalPengeluaranCard(state.totalPengeluaran, state.pengeluaranTrendPercent)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStatCard(
                    title = "Sisa Anggaran",
                    value = state.sisaAnggaran.formatCompact(),
                    badge = state.statusAnggaran.name,
                    icon = Icons.Outlined.ShoppingBag,
                    modifier = Modifier.weight(1f),
                )
                SummaryStatCard(
                    title = "Tingkat Tabungan",
                    value = "${state.tingkatTabungan}%",
                    badge = "+${state.tabunganTrendPercent}%",
                    icon = Icons.Outlined.TrendingUp,
                    modifier = Modifier.weight(1f),
                )
            }

            AlokasiDanaCard(
                total = state.totalAlokasi,
                items = state.alokasiDana,
                mode = state.alokasiMode,
                onTabChange = { onEvent(StatistikEvent.OnAlokasiTabChange(it)) },
                onModeRencanaClick = { onEvent(StatistikEvent.OnModeRencanaToggle) },
            )

            TindakanCepatCard(onClick = { onEvent(StatistikEvent.OnUbahAlokasiClick) })

//            Text("Insight Pintar", style = MaterialTheme.typography.titleMedium)

//            state.aiSummaryQuote?.let { quote ->
//                item { AiQuoteCard(quote) }
//            }

            MonthPicker(
                visible = state.showMonthPicker,
                currentMonth = state.selectedMonth.monthValue - 1,
                currentYear = state.selectedMonth.year,
                confirmButtonCLicked = { month, year ->
                    onEvent(StatistikEvent.OnMonthSelected(YearMonth.of(year, month + 1)))
                },
                cancelClicked = {
                    onEvent(StatistikEvent.OnMonthPickerDismiss)
                }
            )
        }
    }
}

@Composable
fun AlokasiDanaCard(
    total: Long,
    items: List<AlokasiKategoriUi>,
    mode: AlokasiMode,
    onTabChange: (AlokasiMode) -> Unit,
    onModeRencanaClick: () -> Unit,
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
                Text("Alokasi Dana", style = MaterialTheme.typography.titleMedium)
                Text("Berdasarkan Target", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onModeRencanaClick) { Text("Mode Rencana") }


        }

        Spacer(Modifier.height(12.dp))

        SegmentedTabRow(mode, onTabChange)

        Spacer(Modifier.height(16.dp))

        val slices = remember(items) {
            items.map { item ->
                DonutSlice(
                    label = item.nama,
                    value = item.persentase,
                    color = item.colorHex.toColor(),
                )
            }
        }
        Log.d("SLICEDONUT", "slices: $slices")

        InteractiveDonutChart(
            slices = slices,
            modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(220.dp),
        )

        Spacer(Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            items.forEach { item -> LegendItem(item) }
        }

    }
}

@Composable
private fun LegendItem(item: AlokasiKategoriUi) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                    .size(10.dp)
                    .background(item.colorHex.toColor(), CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Column {
            Text(item.nama, style = MaterialTheme.typography.bodySmall)
            Text("${item.persentase}%", style = MaterialTheme.typography.titleSmall)
        }
    }
}


@Composable
fun SegmentedTabRow(mode: AlokasiMode, onTabChange: (AlokasiMode) -> Unit) {
    Row(
        Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
    ) {
        listOf(
            AlokasiMode.ALOKASI to "Alokasi",
            AlokasiMode.REALISASI to "Realisasi",
        ).forEach { (m, label) ->
            val selected = mode == m
            Text(
                text = label,
                textAlign = TextAlign.Center,
                modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onTabChange(m) }
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
            Text("Tindakan Cepat", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "bantuan",
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
                        text = "Ubah Alokasi Dana",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "Sesuaikan target anggaranmu agar finansial tetap sehat.",
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
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        deltaPercent.toString(),
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Total Pengeluaran", fontSize = 13.sp, color = Color.Gray)
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
                isModeRencanaActive = false,
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