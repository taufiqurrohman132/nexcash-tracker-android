package com.example.nexcashapplication.feature.statistics.presentation.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import kotlin.math.pow
import kotlin.math.sqrt


private const val TOTAL_ANGLE = 360.0f
val STROKE_SIZE_UNSELECTED = 40.dp
val STROKE_SIZE_SELECTED = 60.dp

/**
 * Calculate the gap width between the arcs based on [gapPercentage]. The percentage is applied
 * to the average count to determine the width in pixels.
 */
private fun List<AlokasiKategoriUi>.totalAmont(): Float =
    this.sumOf { it.nominal.toDouble() }.toFloat()

fun List<AlokasiKategoriUi>.calculateGap(gapPercentage: Float): Float {
    if (this.isEmpty()) return 0f

    return (this.totalAmont() / this.size) * gapPercentage
}

/**
 * Returns the total data points including the individual gap widths indicated by the
 * [gapPercentage].
 */
fun List<AlokasiKategoriUi>.getTotalAmountWithGapIncluded(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    return this.totalAmont() + (this.size * gap)
}

/**
 * Calculate the sweep angle of an arc including the gap as well. The gap is derived based
 * on [gapPercentage].
 */
fun List<AlokasiKategoriUi>.calculateGapAngle(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    val totalAmountWithGap = this.getTotalAmountWithGapIncluded(gapPercentage)

    return (gap / totalAmountWithGap) * TOTAL_ANGLE
}

/**
 * Returns the sweep angle of a given point in the [DonutChartDataCollection]. This calculations
 * takes the gap between arcs into the account.
 */
fun List<AlokasiKategoriUi>.findSweepAngle(
    index: Int,
    gapPercentage: Float,
): Float {
    val amount = this[index].nominal
    val gap = this.calculateGap(gapPercentage)
    val totalWithGap = getTotalAmountWithGapIncluded(gapPercentage)
    val gapAngle = this.calculateGapAngle(gapPercentage)
    return ((((amount + gap) / totalWithGap) * TOTAL_ANGLE)) - gapAngle
}


/// CANVASS
/**
 * Menemukan jarak antara dua titik dalam grafik.
 * Dihitung menggunakan teorema Pythagoras.
 */
 fun findTouchDistanceFromCenter(center: Offset, touch: Offset) =
    sqrt((touch.x - center.x).pow(2) + (touch.y - center.y).pow(2))

/**
 * Titik sentuh dimulai dari kiri atas kanvas yang berkisar
 * dari (0,0) -> (canvas.width, canvas.height). Kita perlu menormalkan
 * titik ini sehingga (0,0) terletak di tengah grafik.
 */
 fun Offset.findNormalizedPointFromTouch(canvasCenter: Offset) =
    Offset(this.x, canvasCenter.y + (canvasCenter.y - this.y))

/**
 * Hitung sudut sentuh berdasarkan pusat kanvas. Kemudian
 * sesuaikan sudut sehingga gambar dimulai dari kuadran ke-4
 * alih-alih yang pertama.
 */
 fun calculateTouchAngleAccordingToCanvas(
    canvasCenter: Offset,
    normalizedPoint: Offset,
): Float {
    val angle = calculateTouchAngleInDegrees(canvasCenter, normalizedPoint)
    return adjustAngleToCanvas(angle).toFloat()
}

/**
 * Hitung sudut sentuh dalam radian menggunakan atan2(). Setelah itu, konversikan
 * radian ke derajat untuk dibandingkan dengan titik data busur.
 */
 fun calculateTouchAngleInDegrees(canvasCenter: Offset, normalizedPoint: Offset): Double {
    val touchInRadian = kotlin.math.atan2(
        normalizedPoint.y - canvasCenter.y,
        normalizedPoint.x - canvasCenter.x,
    )
    return touchInRadian * -180 / Math.PI // Konversi radian ke sudut dalam derajat
}

/**
 * Mulai dari kuadran ke-4 menuju kuadran ke-1, derajat berkisar dari 0 hingga 360
 */
fun adjustAngleToCanvas(angle: Double) = (angle + TOTAL_ANGLE) % TOTAL_ANGLE