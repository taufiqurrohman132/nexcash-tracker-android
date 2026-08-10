package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.usecase.GetAccountDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DetailAccountViewModel @Inject constructor(
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
            DetailAccountEvent.OnBackClick -> TODO()
            DetailAccountEvent.OnConfirmDelete -> TODO()
            DetailAccountEvent.OnDismissDeleteDialog -> TODO()
            DetailAccountEvent.OnHapusAsetClick -> TODO()
            DetailAccountEvent.OnLihatSemuaClick -> TODO()
            DetailAccountEvent.OnSettingsClick -> TODO()
            is DetailAccountEvent.OnTransactionClick -> TODO()
            DetailAccountEvent.OnTransaksiClick -> TODO()
            DetailAccountEvent.OnTransferClick -> TODO()
            DetailAccountEvent.OnUbahNamaAsetClick -> TODO()
        }
    }

    private fun observerDetailAccount() {
        viewModelScope.launch {
            getAccountDetailUseCase(accountId = accountId).collect { account ->
                updateState {
                    it.copy(
                        isLoading = false,
                        assetId = account.id,
                        assetName = account.name,
                        bankName = account.accountType.toString(),
                        accountLabel = "",
                        accountNumber = account.accountNumber ?: "",
                        balance = account.currentBalance,
//                        recentTransactions = account.recentTransactions,
                        // summary.xxx dipakai di mana?
                    )
                }
            }
        }
    }


}