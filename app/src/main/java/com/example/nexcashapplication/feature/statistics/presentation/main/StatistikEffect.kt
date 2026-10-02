package com.example.nexcashapplication.feature.statistics.presentation.main

sealed interface StatistikEffect {
    data object NavigateToUbahAlokasi : StatistikEffect
}
