package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.usecase.DeleteAccountUseCase
import com.example.nexcashapplication.feature.aset.domain.usecase.GetAccountDetailUseCase
import com.example.nexcashapplication.feature.aset.domain.usecase.ValidationDeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DetailAccountViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val validationDeleteAccountUseCase: ValidationDeleteAccountUseCase,
    private val getAccountDetailUseCase: GetAccountDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DetailAccountState, DetailAccountEvent, DetailAccountEffect>(initialState = DetailAccountState()) {

    private val route: Route.AccountRoute.Detail = savedStateHandle.toRoute()
    private val accountId = route.accountId

    init {
        observerDetailAccount()
    }

    override fun onEvent(event: DetailAccountEvent) {
        when (event) {
            DetailAccountEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(DetailAccountEffect.NavigateBack)
                }
            }

            DetailAccountEvent.OnConfirmDelete -> {
                viewModelScope.launch {
                    if (accountId != null) {
                        deleteAccountUseCase(accountId)
                        sendEffect(DetailAccountEffect.NavigateBackAfterDelete)
                    }
                }
            }

            DetailAccountEvent.OnDismissDeleteDialog -> {
                updateState { it.copy(deleteDialogDesision = null) }
            }

            DetailAccountEvent.OnDeleteAccountClick -> {
                viewModelScope.launch {
                    if (accountId == null) {
//                        sendEffect(DetailAccountEffect.ShowToast("Akun belum termuat, coba lagi"))
                        return@launch
                    }

                    validationDeleteAccountUseCase(accountId).collect { decision ->
                        updateState { it.copy(deleteDialogDesision = decision) }
                    }
                }
            }

            DetailAccountEvent.OnLihatSemuaClick -> {

            }

            DetailAccountEvent.OnSettingsClick -> TODO()
            is DetailAccountEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(DetailAccountEffect.NavigateToTransactionDetail(event.transactionId))
                }
            }

            DetailAccountEvent.OnAddTransactionClick -> {

            }

            DetailAccountEvent.OnTransferClick -> TODO()
            DetailAccountEvent.OnEditAccountClick -> {


                viewModelScope.launch {
                    if (accountId == null) {
//                        sendEffect(DetailAccountEffect.ShowToast("Akun belum termuat, coba lagi"))
                        return@launch
                    }

                    sendEffect(DetailAccountEffect.NavigateToEditAssetName(assetId = accountId))
                }
            }
        }
    }

    private fun observerDetailAccount() {
        viewModelScope.launch {
            if (accountId == null) {
//                        sendEffect(DetailAccountEffect.ShowToast("Akun belum termuat, coba lagi"))
                return@launch
            }
            getAccountDetailUseCase(accountId = accountId).collect { account ->
                updateState {
                    it.copy(
                        isLoading = false,
                        assetId = account.id,
                        assetName = account.name,
                        typeAccount = account.accountType,
                        providers = account.providerAccount,
                        balance = account.currentBalance,
//                        recentTransactions = account.recentTransactions,
                    )
                }
            }
        }
    }


}