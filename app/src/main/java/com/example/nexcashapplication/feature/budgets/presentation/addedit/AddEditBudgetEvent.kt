package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate


sealed interface AddEditBudgetEvent {
    data class BatasBaruChanged(val rawInput: String) : AddEditBudgetEvent
    data class PeriodeTipeChanged(val tipe: RecurrenceType) : AddEditBudgetEvent
    data class TanggalMulaiChanged(val date: LocalDate) : AddEditBudgetEvent
    data class TanggalBerakhirChanged(val date: LocalDate) : AddEditBudgetEvent
    data class TanggalMulaiClicked(val initialDate: LocalDate) : AddEditBudgetEvent
    data class TanggalBerakhirClicked(val initialDate: LocalDate) : AddEditBudgetEvent
    data class BerulangToggled(val enabled: Boolean) : AddEditBudgetEvent
    data object TanggalDismissClicked : AddEditBudgetEvent
    data object SimpanClicked : AddEditBudgetEvent
    data object BatalClicked : AddEditBudgetEvent
    data object ErrorShown : AddEditBudgetEvent
}
