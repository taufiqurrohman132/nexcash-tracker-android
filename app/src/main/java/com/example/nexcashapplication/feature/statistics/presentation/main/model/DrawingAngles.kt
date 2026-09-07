package com.example.nexcashapplication.feature.statistics.presentation.main.model

data class DrawingAngles(
    val start: Float, val end: Float,
) {
    fun isInsideAngle(touchAngle: Float): Boolean {
        return touchAngle in start..end
    }
}
