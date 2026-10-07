package com.example.nexcashapplication.feature.account.presentation.addedit.form

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel

@Immutable
data class AddEditInfoAccountState(
    val accountId: String? = null,
    val namaAset: String = "",
    val typeAccount: TypeAccount = TypeAccount.OTHER,        // locked, dikirim dari screen sebelumnya
    val saldoAwal: Long = 0L,
    val providers: List<AccountProviderUiModel> = emptyList(),
    val selectedProviderId: String? = null,
    val isSaving: Boolean = false,
    val namaAsetError: UiText? = null,

    // edit
    // snapshot awal, diisi pas data ke-load
    val initialNamaAset: String = "",
    val initialSaldoAwal: Long = 0L,
    val initialSelectedProviderId: String? = null,
) {
    val isEditMode: Boolean
        get() = accountId != null

    val hasChanges: Boolean
        get() = namaAset != initialNamaAset ||
                saldoAwal != initialSaldoAwal ||
                selectedProviderId != initialSelectedProviderId

    val isSaveEnabled: Boolean
        get() = namaAset.isNotBlank() && !isSaving &&
                (!isEditMode || hasChanges)
}