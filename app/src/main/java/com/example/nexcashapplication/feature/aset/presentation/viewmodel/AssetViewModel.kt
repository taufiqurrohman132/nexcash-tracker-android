package com.example.nexcashapplication.feature.aset.presentation.viewmodel

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AssetState(
    val isLoading: Boolean = false
)

sealed interface AssetEvent {
    data object Refresh : AssetEvent
}

@HiltViewModel
class AssetViewModel @Inject constructor() : 
    BaseViewModel<AssetState, AssetEvent, Unit>(AssetState()) {

    override fun onEvent(event: AssetEvent) {
        when (event) {
            AssetEvent.Refresh -> {
                // TODO
            }
        }
    }
}
