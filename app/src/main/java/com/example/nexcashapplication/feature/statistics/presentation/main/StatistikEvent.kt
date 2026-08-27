package com.example.nexcashapplication.feature.statistics.presentation.main

sealed interface StatistikEvent {
    data object LoadData : StatistikEvent
    data class GantiBulan(val month: Int, val year: Int) : StatistikEvent
    data class SwitchAlokasiTab(val tab: AlokasiTab) : StatistikEvent
    data object ClickUbahAlokasi : StatistikEvent
    data object ClickBantuan : StatistikEvent
    data class ClickTargetFinansial(val id: Long) : StatistikEvent
    data object ClickLihatSemuaTarget : StatistikEvent
    data object Retry : StatistikEvent
}