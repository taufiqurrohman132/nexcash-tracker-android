package com.example.nexcashapplication.feature.aset.presentation.addedit.form

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderItem
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount

@Immutable
data class AddEditInfoAccountState(
    val namaAset: String = "",
    val typeAccount: String = "",        // locked, dikirim dari screen sebelumnya
    val saldoAwal: String = "",           // simpan sebagai String mentah
    val providers: List<AccountProviderItem> = defaultProviders(),
    val selectedProviderId: String? = null,
    val isSaving: Boolean = false,
    val namaAsetError: String? = null,
) {
    val isSaveEnabled: Boolean
        get() = namaAset.isNotBlank() && !isSaving
}

private fun defaultProviders() = listOf(
    AccountProviderItem("bca", "BCA"),
    AccountProviderItem("mandiri", "Mandiri"),
    AccountProviderItem("gopay", "GoPay"),
    AccountProviderItem("dana", "DANA"),
)