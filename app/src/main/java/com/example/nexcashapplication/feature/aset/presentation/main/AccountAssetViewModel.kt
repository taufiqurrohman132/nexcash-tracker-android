package com.example.nexcashapplication.feature.aset.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.aset.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.aset.presentation.main.mapper.toAccountUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class AccountAssetViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val accountRepository: AccountRepository,
) : BaseViewModel<AccountAssetState, AccountAssetEvent, AccountAssetEffect>(initialState = AccountAssetState()) {

    init {
        observeMonthlySummary()
        observeAccount()
    }

    override fun onEvent(event: AccountAssetEvent) {
        when (event) {
            is AccountAssetEvent.OnAccountClick -> {
                viewModelScope.launch {
                    sendEffect(AccountAssetEffect.NavigateToAccountDetail(event.accountId))
                }
            }
            AccountAssetEvent.OnAddAccountClick -> {
                viewModelScope.launch {
                    sendEffect(AccountAssetEffect.NavigateToAddAccount)
                }
            }
            AccountAssetEvent.OnRetry -> {

            }
            is AccountAssetEvent.OnSeeAllAccountsClick -> {
                viewModelScope.launch {
                    sendEffect(AccountAssetEffect.NavigateToAllAccounts(event.type))
                }
            }
            AccountAssetEvent.OnToggleBalanceVisibility -> {
                viewModelScope.launch {
                    updateState { it.copy(isBalanceVisible = !it.isBalanceVisible) }
                }
            }
        }
    }

    private fun observeMonthlySummary() {
        getMonthlySummaryUseCase()
                .onEach { summary ->
                    updateState {
                        it.copy(
                            totalBalance = summary.totalSaldoGabungan,
                            monthlyChangePercent = summary.percentage,
                            activeAssetCount = summary.activeAccount,
                            isLoading = false,
                        )
                    }
                }
                .catch { e ->
                    updateState { it.copy(isLoading = false, error = e.message) }
                }
                .launchIn(viewModelScope)
    }

    private fun observeAccount() {
        updateState {
            it.copy(isLoading = true, error = null)
        }

        accountRepository.getAllAccounts()
                .catch { throwable ->
                    updateState { it.copy(isLoading = false, error = throwable.message) }
                    sendEffect(
                        AccountAssetEffect.ShowError(
                            throwable.message ?: "Gagal memuat data",
                        ),
                    )
                }
                .onEach { accounts ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = null,
                            bankAccounts = accounts.toAccountUiModelList(),
//                                        .filter { account ->
////                                    account.provider == TypeAccount.BANK
//                                },
                        )
                    }
                }
                .launchIn(viewModelScope)

    }


}