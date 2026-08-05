package com.example.nexcashapplication.feature.tagihan.presentation.viewmodel

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TagihanState(
    val isLoading: Boolean = false
)

sealed interface TagihanEvent {
    data object Refresh : TagihanEvent
}

@HiltViewModel
class TagihanViewModel @Inject constructor() : 
    BaseViewModel<TagihanState, TagihanEvent, Unit>(TagihanState()) {

    override fun onEvent(event: TagihanEvent) {
        when (event) {
            TagihanEvent.Refresh -> {
                // TODO
            }
        }
    }
}
