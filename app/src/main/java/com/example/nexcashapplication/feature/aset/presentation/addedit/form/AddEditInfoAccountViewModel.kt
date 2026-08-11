package com.example.nexcashapplication.feature.aset.presentation.addedit.form

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.usecase.SaveAccontUseCase
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditInfoAccountViewModel @Inject constructor(
    private val saveAssetUseCase: SaveAccontUseCase,
) : BaseViewModel<AddEditInfoAccountState, AddEditInfoAccountEvent, AddEditInfoAccountEffect>(
    initialState = AddEditInfoAccountState(),
) {

    override fun onEvent(event: AddEditInfoAccountEvent) {
        when (event) {
            AddEditInfoAccountEvent.BackClicked -> TODO()
            AddEditInfoAccountEvent.LihatSemuaProviderClicked -> TODO()
            is AddEditInfoAccountEvent.NamaAsetChanged -> updateState {
                it.copy(namaAset = event.value)
            }

            is AddEditInfoAccountEvent.ProviderSelected -> TODO()
            is AddEditInfoAccountEvent.SaldoAwalChanged -> updateState {
                it.copy(saldoAwal = event.value)
            }

            AddEditInfoAccountEvent.SimpanClicked -> handleSimpan()
        }
    }

    private fun handleSimpan() {
        val current = state.value

        if (current.namaAset.isBlank()) {
            updateState { it.copy(namaAsetError = "Nama aset wajib diisi") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isSaving = true) }

            saveAssetUseCase(
                account = AccountModel(
                    name = current.namaAset,
                    currentBalance = current.saldoAwal.toLong(),
                    institution = current.providers.find { it.id == current.selectedProviderId },
                    accountType = TypeAccount.valueOf(current.tipeAset.uppercase()),
                    isActive = true,
                ),
            ).onSuccess {
//                sendEffect(AddEditInfoAccountEffect.AssetSavedNavigateHome)
            }.onFailure { throwable ->
                sendEffect(
                    AddEditInfoAccountEffect.ShowError(
                        throwable.message ?: "Gagal menyimpan aset",
                    ),
                )
            }
        }
    }

}