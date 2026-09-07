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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.PieSize
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieSeries
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun InteractiveDonutChart(
    slices: List<DonutSlice>,
    startAngleDeg: Float = -90f,
    modifier: Modifier = Modifier,
) {

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    val sliceAngles = remember(slices) {
        var comulative = startAngleDeg
        slices.map { slice ->
            val sweep = 360f * (slice.value / total)
            val range = comulative to (comulative + sweep)
            comulative += sweep
            range
        }
    }

    val modelProducer = remember { PieChartModelProducer() }
    LaunchedEffect(slices, selectedIndex) {
        modelProducer.runTransaction {
            pieSeries {
                slices.forEachIndexed { index, slice ->
                    // offset dikit kalau slice ini yang lagi dipilih -> efek "highlight"
                    series(slice.value)
                }
            }
        }
    }

    Box(
        modifier = modifier.pointerInput(sliceAngles) {
            detectTapGestures { offset ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val dx = offset.x - center.x
                val dy = offset.y - center.y
                val distance = sqrt(dx * dx + dy * dy)

                // radius pie asumsinya setengah dari sisi terpendek Box
                // sesuaikan dengan innerSize/outerSize yang kamu set di PieChart
                val outerRadius = minOf(size.width, size.height) / 2f
                val innerRadius = outerRadius * 0.5f // samain rasio innerSize.fixed()-mu

                if (distance in innerRadius..outerRadius) {
                    // atan2 ngasih -180..180, normalisasi ke 0..360 biar cocok sama cumulative angle
                    var tapAngle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                    if (tapAngle < startAngleDeg) tapAngle += 360f

                    val hitIndex = sliceAngles.indexOfFirst { (start, end) ->
                        tapAngle in start..end
                    }
                    selectedIndex = if (hitIndex == selectedIndex) null else hitIndex
                }
            }
        },
    ) {

        ComposeBasicDonutChart(modelProducer)

        // opsional: tampilkan detail slice yang dipilih
        selectedIndex?.let { idx ->
            val slice = slices[idx]
            Text(
                text = "${slice.label}: ${slice.value.toInt()}%",
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}


@Composable
private fun ComposeBasicDonutChart(
    modelProducer: PieChartModelProducer,
    modifier: Modifier = Modifier,
) {
    PieChartHost(
        chart = rememberPieChart(
            sliceProvider = PieChart.SliceProvider.series(
                vicoTheme.pieChartColors.map { color ->
                    PieChart.Slice(

                        fill = Fill(color),
                        label = PieChart.SliceLabel.Outside(
                            textComponent = TextComponent(
                                textStyle = TextStyle(vicoTheme.textColor),
                                margins = Insets(horizontal = 4.dp),
                            ),
                            lineColor = vicoTheme.textColor,
                        ),
                    )
                },
            ),

            innerSize = PieSize.Inner.fixed(128.dp),
            spacing = 8.dp,
            valueFormatter = PieValueFormatter { _, value, _ -> "${value.toInt()}%" },
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}