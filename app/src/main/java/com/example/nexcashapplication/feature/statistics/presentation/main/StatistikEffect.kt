package com.example.nexcashapplication.feature.statistics.presentation.main

import java.time.YearMonth

sealed interface StatistikEffect {
    data object NavigateToUbahAlokasi : StatistikEffect
    data object NavigateToTargetList : StatistikEffect
    data class NavigateToTargetDetail(val id: String) : StatistikEffect
    data class ShowMonthPicker(val current: YearMonth) : StatistikEffect
}