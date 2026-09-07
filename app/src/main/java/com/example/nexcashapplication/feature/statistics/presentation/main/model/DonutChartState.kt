package com.example.nexcashapplication.feature.statistics.presentation.main.model

import androidx.compose.ui.unit.Dp
import com.example.nexcashapplication.feature.statistics.presentation.utils.STROKE_SIZE_SELECTED
import com.example.nexcashapplication.feature.statistics.presentation.utils.STROKE_SIZE_UNSELECTED

class DonutChartState(
    val state: State = State.UNSELECTED,
) {
    val stroke: Dp = when (state) {
        State.SELECTED -> STROKE_SIZE_SELECTED
        State.UNSELECTED -> STROKE_SIZE_UNSELECTED
    }

    enum class State {
        SELECTED, UNSELECTED
    }
}