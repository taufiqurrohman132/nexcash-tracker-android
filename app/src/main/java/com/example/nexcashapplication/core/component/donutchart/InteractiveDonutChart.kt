package com.example.nexcashapplication.core.component.donutchart

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.PieSize
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieSeries
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import kotlin.math.atan2
import kotlin.math.sqrt

/** Ukuran lubang tengah — harus sama dengan [PieSize.Inner.fixed] di bawah. */
private val InnerHoleSize = 128.dp

/** Jarak congkelan slice yang dipilih (efek "explode"). */
private val SelectedSliceOffset = 10.dp

@Composable
fun InteractiveDonutChart(
    slices: List<DonutSlice>,
    startAngleDeg: Float = -90f,
    modifier: Modifier = Modifier,
) {

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // data/tab berubah -> highlight lama sudah tidak relevan (bisa salah index)
    LaunchedEffect(slices) { selectedIndex = null }

    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    val sliceAngles = remember(slices, startAngleDeg) {
        if (total <= 0f) {
            emptyList()
        } else {
            var comulative = startAngleDeg
            slices.map { slice ->
                val sweep = 360f * (slice.value / total)
                val range = comulative to (comulative + sweep)
                comulative += sweep
                range
            }
        }
    }

    val modelProducer = remember { PieChartModelProducer() }
    LaunchedEffect(slices) {
        modelProducer.runTransaction {
            // Satu panggilan untuk semua nilai. Builder.series() menimpa entries
            // tiap pemanggilan, jadi kalau dipanggil di dalam loop hasilnya
            // cuma 1 entri (slice terakhir) -> chart jadi lingkaran satu warna.
            pieSeries { series(slices.map { it.value }) }
        }
    }

    // Warna mengikuti kategori (DonutSlice.color), slice terpilih dicondongkan keluar.
    val vicoSlices = remember(slices, selectedIndex) {
        slices.mapIndexed { index, slice ->
            PieChart.Slice(
                fill = Fill(slice.color),
                offsetFromCenter = if (index == selectedIndex) SelectedSliceOffset else 0.dp,
                label = PieChart.SliceLabel.Outside(
                    textComponent = TextComponent(
                        textStyle = TextStyle(slice.color),
                        margins = Insets(horizontal = 4.dp),
                    ),
                    lineColor = slice.color,
                ),
            )
        }
    }

    Box(
        modifier = modifier.pointerInput(sliceAngles) {
            // rasio hole dibuat dari nilai dp yang sama, bukan tebakan 0.5f
            val innerRadius = InnerHoleSize.toPx() / 2f

            detectTapGestures { offset ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val dx = offset.x - center.x
                val dy = offset.y - center.y
                val distance = sqrt(dx * dx + dy * dy)

                // radius luar asumsinya setengah dari sisi terpendek Box
                // (Vico mengecilkan sedikit karena ada outside label)
                val outerRadius = minOf(size.width, size.height) / 2f

                if (distance in innerRadius..outerRadius) {
                    // atan2 ngasih -180..180, normalisasi ke rentang sudut kumulatif
                    var tapAngle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                    if (tapAngle < startAngleDeg) tapAngle += 360f

                    // takeIf: -1 artinya area tanpa slice, jangan jadi index
                    val hitIndex = sliceAngles.indexOfFirst { (start, end) ->
                        tapAngle in start..end
                    }.takeIf { it >= 0 }

                    selectedIndex = if (hitIndex == selectedIndex) null else hitIndex
                }
            }
        },
    ) {

        if (slices.isNotEmpty()) {
            ComposeBasicDonutChart(
                vicoSlices = vicoSlices,
                modelProducer = modelProducer,
                startAngleDeg = startAngleDeg,
            )
        }

        // detail slice yang dipilih, tampil di lubang tengah
        selectedIndex?.let { idx ->
            slices.getOrNull(idx)?.let { slice ->
                Text(
                    text = stringResource(
                        R.string.core_donut_slice_percent,
                        slice.label,
                        slice.value.toInt(),
                    ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}


@Composable
private fun ComposeBasicDonutChart(
    vicoSlices: List<PieChart.Slice>,
    modelProducer: PieChartModelProducer,
    startAngleDeg: Float,
    modifier: Modifier = Modifier,
) {
    val sliceProvider = remember(vicoSlices) {
        // SliceProvider.series() mewajibkan minimal 1 slice; state kosong sudah
        // di-guard di InteractiveDonutChart, fallback ini sekadar jaga-jaga.
        PieChart.SliceProvider.series(vicoSlices.ifEmpty { listOf(PieChart.Slice()) })
    }

    PieChartHost(
        chart = rememberPieChart(
            sliceProvider = sliceProvider,
            startAngle = startAngleDeg,
            innerSize = PieSize.Inner.fixed(InnerHoleSize),
            spacing = 8.dp,
            valueFormatter = PieValueFormatter { _, value, _ -> "${value.toInt()}%" },
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}
