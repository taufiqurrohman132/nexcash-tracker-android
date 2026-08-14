package com.example.nexcashapplication.feature.aset.presentation.addedit.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.usecase.SaveAccontUseCase
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditInfoAccountViewModel @Inject constructor(
    private val saveAssetUseCase: SaveAccontUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditInfoAccountState, AddEditInfoAccountEvent, AddEditInfoAccountEffect>(
    initialState = AddEditInfoAccountState(),
) {
    private val route: Route.AccountRoute.AddEditInfo = savedStateHandle.toRoute()
    private val typeAccount = route.typeAccount

    init {
        updateState {
            it.copy(typeAccount = typeAccount.name)
        }
    }

    override fun onEvent(event: AddEditInfoAccountEvent) {
        when (event) {
            AddEditInfoAccountEvent.BackClicked -> {
                viewModelScope.launch {
                    sendEffect(AddEditInfoAccountEffect.NavigateBack)
                }
            }

            AddEditInfoAccountEvent.LihatSemuaProviderClicked -> {
                viewModelScope.launch {
                    sendEffect(AddEditInfoAccountEffect.NavigateToProviderList)
                }
            }

            is AddEditInfoAccountEvent.NamaAsetChanged -> updateState {
                it.copy(namaAset = event.value)
            }

            is AddEditInfoAccountEvent.ProviderSelected -> updateState {
                it.copy(selectedProviderId = event.providerId)
            }

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
                    providerAccount = current.providers.find { it.id == current.selectedProviderId },
                    accountType = TypeAccount.valueOf(current.typeAccount.uppercase()),
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