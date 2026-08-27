package com.example.nexcashapplication.feature.budgets.presentation.addedit

import java.time.LocalDate


sealed interface AddEditBudgetEvent {
    data class BatasBaruChanged(val rawInput: String) : AddEditBudgetEvent
    data class PeriodeTipeChanged(val tipe: PeriodeTipe) : AddEditBudgetEvent
    data class TanggalMulaiChanged(val date: LocalDate) : AddEditBudgetEvent
    data class TanggalBerakhirChanged(val date: LocalDate) : AddEditBudgetEvent
    data class BerulangToggled(val enabled: Boolean) : AddEditBudgetEvent
    data object SimpanClicked : AddEditBudgetEvent
    data object BatalClicked : AddEditBudgetEvent
    data object ErrorShown : AddEditBudgetEvent
}
