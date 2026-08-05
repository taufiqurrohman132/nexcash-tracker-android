package com.example.nexcashapplication.feature.insight.presentation.viewmodel

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class InsightState(
    val isLoading: Boolean = false
)

sealed interface InsightEvent {
    data object Refresh : InsightEvent
}

@HiltViewModel
class InsightViewModel @Inject constructor() : 
    BaseViewModel<InsightState, InsightEvent, Unit>(InsightState()) {

    override fun onEvent(event: InsightEvent) {
        when (event) {
            InsightEvent.Refresh -> {
                // TODO
            }
        }
    }
}
