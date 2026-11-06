package com.example.nexcashapplication.feature.account.presentation.addedit.form

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.usecase.GetAccountDetailUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.SaveAccontUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.UpdateAccontUseCase
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditInfoAccountViewModel internal constructor(
    private val route: Route.AccountRoute.AddEditInfo,
    private val updateAssetUseCase: UpdateAccontUseCase,
    private val saveAssetUseCase: SaveAccontUseCase,
    private val getAccountDetailUseCase: GetAccountDetailUseCase,
) : BaseViewModel<AddEditInfoAccountState, AddEditInfoAccountEvent, AddEditInfoAccountEffect>(
    initialState = AddEditInfoAccountState(),
) {

    @Inject constructor(
        updateAssetUseCase: UpdateAccontUseCase,
        saveAssetUseCase: SaveAccontUseCase,
        getAccountDetailUseCase: GetAccountDetailUseCase,
        savedStateHandle: SavedStateHandle,
    ) : this(
        route = savedStateHandle.toRoute<Route.AccountRoute.AddEditInfo>(),
        updateAssetUseCase = updateAssetUseCase,
        saveAssetUseCase = saveAssetUseCase,
        getAccountDetailUseCase = getAccountDetailUseCase,
    )

    private val typeAccount = route.typeAccount ?: TypeAccount.OTHER
    private val accountId: String? = route.accountId

    init {
        observerDetailAccount(accountId, typeAccount)
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
                it.copy(saldoAwal = event.value.toLongOrNull() ?: 0L)
            }

            AddEditInfoAccountEvent.SimpanClicked -> handleSaveOrUpdate()
        }
    }

    private fun handleSaveOrUpdate() {
        val current = state.value

        if (current.namaAset.isBlank()) {
            updateState { it.copy(namaAsetError = uiText(R.string.acct_name_required)) }
            return
        }

        viewModelScope.launch {

            Log.d(TAG, "handleSimpan: saldo awal = ${current.saldoAwal}")
            if (current.isEditMode) {
                updateAssetUseCase(
                    account = AccountModel(
                        id = current.accountId,
                        name = current.namaAset,
                        currentBalance = current.saldoAwal,
                        providerAccount = current.selectedProviderId?.let { id ->
                            ProviderCatalog.getById(id)
                        },
                        accountType = current.typeAccount,
                        isActive = true,
                    ),
                ).onSuccess {
                    sendEffect(AddEditInfoAccountEffect.NavigateBack)
                    updateState { it.copy(isSaving = true) }
                }.onFailure { throwable ->
                    sendEffect(
                        AddEditInfoAccountEffect.ShowError(
                            throwable.toUiText(R.string.acct_save_failed),
                        ),
                    )
                }
            } else saveAssetUseCase(
                account = AccountModel(
                    name = current.namaAset,
                    currentBalance = current.saldoAwal,
                    providerAccount = current.selectedProviderId?.let { id ->
                        ProviderCatalog.getById(id)
                    },
                    accountType = current.typeAccount,
                    isActive = true,
                ),
            ).onSuccess {
                sendEffect(AddEditInfoAccountEffect.AssetSavedNavigateMain)
                updateState { it.copy(isSaving = true) }
            }.onFailure { throwable ->
                sendEffect(
                    AddEditInfoAccountEffect.ShowError(
                        throwable.toUiText(R.string.acct_save_failed),
                    ),
                )
            }
        }
    }

    private fun observerDetailAccount(id: String?, type: TypeAccount) {
        viewModelScope.launch {
            if (id != null) {
                getAccountDetailUseCase(accountId = id)
                        // Wajib: tanpa catch kegagalan Room bocor ke viewModelScope = crash.
                        // Form edit tetap bisa dipakai (nama/saldo manual), jadi cukup
                        // beri tahu lewat snackbar, jangan blokir layar.
                        .catch { e ->
                            Log.e(TAG, "gagal memuat detail akun untuk diedit", e)
                            sendEffect(
                                AddEditInfoAccountEffect.ShowError(
                                    e.toUiText(R.string.acct_load_failed),
                                ),
                            )
                        }
                        .collect { accountDetailData ->
                            updateState {
                                it.copy(
                                    accountId = accountDetailData.account.id,
                                    namaAset = accountDetailData.account.name,
                                    typeAccount = accountDetailData.account.accountType,
                                    providers = ProviderCatalog.getByType(accountDetailData.account.accountType),
                                    selectedProviderId = accountDetailData.account.providerAccount?.id,
                                    saldoAwal = accountDetailData.account.currentBalance,
                                    initialNamaAset = accountDetailData.account.name,
                                    initialSaldoAwal = accountDetailData.account.currentBalance,
                                    initialSelectedProviderId = accountDetailData.account.providerAccount?.id,
                                )
                            }
                        }
            } else {
                updateState {
                    it.copy(
                        typeAccount = typeAccount,
                        providers = ProviderCatalog.getByType(type),
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = AddEditInfoAccountViewModel::class.java.simpleName
    }
}
