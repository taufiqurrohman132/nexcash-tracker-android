package com.example.nexcashapplication.feature.aset.presentation.addedit.form

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderUiModel

@Immutable
data class AddEditInfoAccountState(
    val namaAset: String = "",
    val typeAccount: String = "",        // locked, dikirim dari screen sebelumnya
    val saldoAwal: Long = 0L,           // simpan sebagai String mentah
    val providers: List<AccountProviderUiModel> = emptyList(),
    val selectedProviderId: String? = null,
    val isSaving: Boolean = false,
    val namaAsetError: String? = null,
) {
    val isSaveEnabled: Boolean
        get() = namaAset.isNotBlank() && !isSaving
}
//
//private fun defaultProviders() = listOf(
//    AccountProviderUiModel("bca", "BCA"),
//    AccountProviderUiModel("mandiri", "Mandiri"),
//    AccountProviderUiModel("gopay", "GoPay"),
//    AccountProviderUiModel("dana", "DANA"),
//)