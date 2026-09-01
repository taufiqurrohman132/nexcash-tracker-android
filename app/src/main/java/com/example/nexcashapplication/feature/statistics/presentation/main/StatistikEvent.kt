package com.example.nexcashapplication.feature.statistics.presentation.main

import java.time.YearMonth

sealed interface StatistikEvent {
    data object OnGantiBulanClick : StatistikEvent
    data class OnMonthSelected(val month: YearMonth) : StatistikEvent
    data class OnAlokasiTabChange(val mode: AlokasiMode) : StatistikEvent
    data object OnModeRencanaToggle : StatistikEvent
    data object OnUbahAlokasiClick : StatistikEvent
    data object OnLihatSemuaTargetClick : StatistikEvent
    data class OnTargetClick(val id: String) : StatistikEvent
}