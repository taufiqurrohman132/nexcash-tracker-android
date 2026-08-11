package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

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

@HiltViewModel
class AddEditAccountViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val accountRepository: AccountRepository,
) : BaseViewModel<AddEditAccountState, AddEditAccountEvent, AddEditAccountEffect>(initialState = AddEditAccountState()) {

    init {
        observeMonthlySummary()
        observeAccount()
    }

    override fun onEvent(event: AddEditAccountEvent) {
        when (event) {
            is AddEditAccountEvent.OnAddEditAccountClick -> TODO()
            AddEditAccountEvent.OnAddAddEditAccountClick -> TODO()
            AddEditAccountEvent.OnAddClickAddEdit -> TODO()
            is AddEditAccountEvent.OnInvestmentClick -> TODO()
            AddEditAccountEvent.OnRetry -> TODO()
            AddEditAccountEvent.OnSeeAllBankAccountsClick -> TODO()
            AddEditAccountEvent.OnToggleBalanceVisibility -> TODO()
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
                        AddEditAccountEffect.ShowError(
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