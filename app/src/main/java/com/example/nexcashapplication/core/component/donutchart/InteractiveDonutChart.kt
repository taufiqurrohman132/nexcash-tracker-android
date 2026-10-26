package com.example.nexcashapplication.core.component.donutchart

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Diameter lubang tengah — DIKUNCI tetap; slice terpilih menebal ke luar, tidak ke sini. */
private val InnerHoleSize = 128.dp

/** Tebal cincin normal (radius luar - radius dalam). */
private val BaseRingWidth = 20.dp

/**
 * Berapa cincin MENEBAL KELUAR saat dipilih: radius dalam tetap di [InnerHoleSize],
 * hanya tepi luar yang tumbuh (spring) — bukan digeser seperti efek "explode".
 */
private val SelectedExtraStroke = 12.dp

/** Jarak antar slice, pengganti `spacing` Vico. */
private val SliceSpacing = 8.dp

/** Posisi label persentase, diukur dari tepi luar cincin dasar. */
private val LabelRadiusOffset = 20.dp

/** Grafik donat interaktif: tap slice untuk menampilkannya di lubang tengah. */
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

    // Progres menebal per slice: 0 = normal, 1 = terpilih. Tiap slice punya
    // state animasinya sendiri sehingga deselect berjalan paralel dengan select.
    val selectionProgress = slices.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = if (index == selectedIndex) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            label = "donut-selection-$index",
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                    .matchParentSize()
                    .pointerInput(sliceAngles) {
                        val innerRadius = InnerHoleSize.toPx() / 2f
                        val outerLimit = innerRadius +
                                BaseRingWidth.toPx() +
                                SelectedExtraStroke.toPx()

                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val distance = sqrt(dx * dx + dy * dy)

                            if (distance in innerRadius..outerLimit) {
                                // atan2 ngasih -180..180, normalisasi ke rentang sudut kumulatif
                                var tapAngle =
                                    Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                                if (tapAngle < startAngleDeg) tapAngle += 360f

                                // takeIf: -1 artinya area tanpa slice, jangan jadi index
                                val hitIndex = sliceAngles.indexOfFirst { (start, end) ->
                                    tapAngle in start..end
                                }.takeIf { it >= 0 }

                                selectedIndex =
                                    if (hitIndex == selectedIndex) null else hitIndex
                            }
                        }
                    },
        ) {
            if (total <= 0f) return@Canvas

            val center = Offset(size.width / 2f, size.height / 2f)
            val innerRadius = InnerHoleSize.toPx() / 2f
            val baseStroke = BaseRingWidth.toPx()
            val extraStroke = SelectedExtraStroke.toPx()
            val spacingPx = SliceSpacing.toPx()
            // radius cincin dasar; label digambar pada radius tetap ini
            val baseOuter = innerRadius + baseStroke
            val labelRadius = baseOuter + LabelRadiusOffset.toPx()
            // sudut gap dihitung pada tengah cincin, seperti spacing Vico
            val midRadius = innerRadius + baseStroke / 2f
            val gapAngle = if (midRadius > 0f) {
                Math.toDegrees((spacingPx / midRadius).toDouble()).toFloat()
            } else {
                0f
            }

            var currentAngle = startAngleDeg
            slices.forEachIndexed { index, slice ->
                val sweep = 360f * (slice.value / total)
                val drawSweep = (sweep - gapAngle).coerceAtLeast(0f)
                val drawStart = currentAngle + gapAngle / 2f
                currentAngle += sweep

                if (drawSweep <= 0f) return@forEachIndexed

                val progress = selectionProgress[index].value
                val strokeW = baseStroke + extraStroke * progress
                // Radius path = tepi dalam + setengah tebal -> tepi dalam selalu
                // menempel di innerRadius, pertumbuhannya hanya ke luar.
                val arcRadius = innerRadius + strokeW / 2f

                drawArc(
                    color = slice.color,
                    startAngle = drawStart,
                    sweepAngle = drawSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2f, arcRadius * 2f),
                    style = Stroke(width = strokeW),
                )

                // Label persentase di luar cincin — ikut memudar saat slice menebal,
                // karena persentasenya sudah tampil di lubang tengah.
                val labelAlpha = 1f - progress
                if (labelAlpha > 0.01f) {
                    val midAngleRad = Math.toRadians((drawStart + drawSweep / 2f).toDouble())
                    val dirX = cos(midAngleRad).toFloat()
                    val dirY = sin(midAngleRad).toFloat()

                    val outerEdge = innerRadius + strokeW
                    val layoutResult = textMeasurer.measure(
                        text = AnnotatedString("${slice.value.roundToInt()}%"),
                        style = TextStyle(
                            color = slice.color.copy(alpha = labelAlpha),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        density = this,
                    )
                    val halfLabelHeight = layoutResult.size.height / 2f

                    // garis penunjuk pendek: dari tepi luar cincin ke tepi label
                    val lineStart = outerEdge + 1.dp.toPx()
                    val lineEnd = minOf(outerEdge + 5.dp.toPx(), labelRadius - halfLabelHeight)
                    if (lineEnd > lineStart) {
                        drawLine(
                            color = slice.color.copy(alpha = labelAlpha),
                            start = Offset(
                                center.x + dirX * lineStart,
                                center.y + dirY * lineStart,
                            ),
                            end = Offset(
                                center.x + dirX * lineEnd,
                                center.y + dirY * lineEnd,
                            ),
                            strokeWidth = 1.5.dp.toPx(),
                        )
                    }

                    val labelCenter = Offset(
                        center.x + dirX * labelRadius,
                        center.y + dirY * labelRadius,
                    )
                    drawText(
                        textLayoutResult = layoutResult,
                        topLeft = Offset(
                            labelCenter.x - layoutResult.size.width / 2f,
                            labelCenter.y - halfLabelHeight,
                        ),
                    )
                }
            }
        } // Canvas

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

// region Preview

private val previewSlices = listOf(
    DonutSlice(label = "Makanan", value = 35f, color = Color(0xFFFF6B6B)),
    DonutSlice(label = "Transportasi", value = 20f, color = Color(0xFF4ECDC4)),
    DonutSlice(label = "Hiburan", value = 15f, color = Color(0xFFFFD93D)),
    DonutSlice(label = "Tabungan", value = 30f, color = Color(0xFF6BCB77)),
)

/**
 * Padding longgar supaya label persentase di luar cincin (radius ±96dp dari
 * pusat) tidak terpotong tepi layar preview.
 */
@Preview(showBackground = true, name = "Donut - 4 slice")
@Composable
private fun InteractiveDonutChartPreview() {
    NexCashApplicationTheme {
        // Tap salah satu slice di preview interaktif: cincinnya menebal ke luar
        // (lubang tetap), label luarnya memudar, persentasenya tampil di tengah.
        InteractiveDonutChart(
            slices = previewSlices,
            modifier = Modifier
                    .padding(24.dp)
                    .size(220.dp),
        )
    }
}

@Preview(showBackground = true, name = "Donut - 2 slice")
@Composable
private fun InteractiveDonutChartFewSlicesPreview() {
    NexCashApplicationTheme {
        InteractiveDonutChart(
            slices = previewSlices.take(2),
            modifier = Modifier
                    .padding(24.dp)
                    .size(220.dp),
        )
    }
}

@Preview(showBackground = true, name = "Donut - kosong")
@Composable
private fun InteractiveDonutChartEmptyPreview() {
    NexCashApplicationTheme {
        // Total 0 / list kosong: kanvas kosong, tanpa crash
        InteractiveDonutChart(
            slices = emptyList(),
            modifier = Modifier
                    .padding(24.dp)
                    .size(220.dp),
        )
    }
}

// endregion
