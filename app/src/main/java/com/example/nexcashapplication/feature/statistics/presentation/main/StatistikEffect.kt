package com.example.nexcashapplication.feature.statistics.presentation.main

sealed interface StatistikEffect {
    data object NavigateToUbahAlokasi : StatistikEffect
    data class NavigateToTargetDetail(val id: Long) : StatistikEffect
    data object NavigateToSemuaTarget : StatistikEffect
    data object ShowHelpSheet : StatistikEffect
    data class ShowError(val message: String) : StatistikEffect
}