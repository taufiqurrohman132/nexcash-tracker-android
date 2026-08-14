package com.example.nexcashapplication.feature.aset.presentation.addedit.form

sealed interface AddEditInfoAccountEvent {
    data class NamaAsetChanged(val value: String) : AddEditInfoAccountEvent
    data class SaldoAwalChanged(val value: Long) : AddEditInfoAccountEvent
    data class ProviderSelected(val providerId: String) : AddEditInfoAccountEvent
    data object LihatSemuaProviderClicked : AddEditInfoAccountEvent
    data object BackClicked : AddEditInfoAccountEvent
    data object SimpanClicked : AddEditInfoAccountEvent
}