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
    private val _selectedPeriod = MutableStateFlow(YearMonth.now())
    private val _activeTab = MutableStateFlow(AlokasiTab.ALOKASI)


    override fun onEvent(event: StatistikEvent) {
        when (event) {
            StatistikEvent.ClickBantuan -> TODO()
            StatistikEvent.ClickLihatSemuaTarget -> TODO()
            is StatistikEvent.ClickTargetFinansial -> TODO()
            StatistikEvent.ClickUbahAlokasi -> TODO()
            is StatistikEvent.GantiBulan -> TODO()
            StatistikEvent.LoadData -> TODO()
            StatistikEvent.Retry -> TODO()
            is StatistikEvent.SwitchAlokasiTab -> TODO()
        }
    }


}