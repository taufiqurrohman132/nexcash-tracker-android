package com.example.nexcashapplication.feature.statistics.presentation.main

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class StatistikViewModel @Inject constructor(

) : BaseViewModel<StatistikState, StatistikEvent, StatistikEffect>(
    StatistikState(),
) {
    private val selectedPeriod = MutableStateFlow(YearMonth.now())
    private val alokasiMode = MutableStateFlow(AlokasiMode.ALOKASI)


    override fun onEvent(event: StatistikEvent) {
        when (event) {
            is StatistikEvent.OnAlokasiTabChange -> TODO()
            StatistikEvent.OnGantiBulanClick -> TODO()
            StatistikEvent.OnLihatSemuaTargetClick -> TODO()
            StatistikEvent.OnModeRencanaToggle -> TODO()
            is StatistikEvent.OnMonthSelected -> TODO()
            is StatistikEvent.OnTargetClick -> TODO()
            StatistikEvent.OnUbahAlokasiClick -> TODO()
        }
    }

    private fun loadData() {
        
    }


}