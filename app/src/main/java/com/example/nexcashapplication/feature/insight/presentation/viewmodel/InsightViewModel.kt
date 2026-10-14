package com.example.nexcashapplication.feature.insight.presentation.viewmodel

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class InsightState(
    val isLoading: Boolean = false
)

// Belum punya event sama sekali: `Refresh` dihapus karena tidak pernah
// dipanggil layar mana pun dan aplikasi ini offline — Room sudah reactive.
sealed interface InsightEvent

/**
 * Stub fitur Insight. `InsightScreen()` masih dikomentari di
 * `core/navigations/NavigationStack.kt`, jadi ViewModel ini belum punya
 * pemakaian nyata — lihat catatan rilis terpisah untuk penghapusan paketnya.
 */
@HiltViewModel
class InsightViewModel @Inject constructor() :
    BaseViewModel<InsightState, InsightEvent, Unit>(InsightState()) {

    override fun onEvent(event: InsightEvent) = Unit
}
