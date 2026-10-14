package com.example.nexcashapplication.feature.account.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import android.util.Log
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.usecase.DeleteAccountUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.GetAccountDetailUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.ValidationDeleteAccountUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "DetailAccountVm"

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

            // Muat ulang setelah gagal: reset loading + error, lalu pasang ulang observer.
            DetailAccountEvent.OnRetryLoad -> {
                updateState { it.copy(isLoading = true, errorMessage = null) }
                observerDetailAccount()
            }

            DetailAccountEvent.OnDeleteAccountClick -> {
                viewModelScope.launch {
                    if (accountId == null) {
                        sendEffect(DetailAccountEffect.ShowToast(uiText(R.string.acct_account_not_loaded)))
                        return@launch
                    }

                    validationDeleteAccountUseCase(accountId)
                            // Gagal baca validasi hapus → dialog tidak muncul, tapi
                            // jangan crash; cukup catat (layar detail tetap utuh).
                            .catch { e ->
                                Log.e(TAG, "gagal memvalidasi hapus akun", e)
                            }
                            .collect { decision ->
                                updateState { it.copy(deleteDialogDesision = decision) }
                            }
                }
            }

            DetailAccountEvent.OnLihatSemuaClick -> {
                viewModelScope.launch {
                    if (accountId == null) {
                        sendEffect(DetailAccountEffect.ShowToast(uiText(R.string.acct_account_not_loaded)))
                        return@launch
                    }

                    sendEffect(DetailAccountEffect.NavigateToAllTransactions(assetId = accountId))
                }
            }

            DetailAccountEvent.OnSettingsClick -> TODO()
            is DetailAccountEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    if (event.transactionId == null) {
                        sendEffect(DetailAccountEffect.ShowSnackbar(uiText(R.string.acct_transaction_unavailable)))
                    } else
                        sendEffect(DetailAccountEffect.NavigateToTransactionDetail(event.transactionId))
                }
            }

            DetailAccountEvent.OnAddTransactionClick -> {
                viewModelScope.launch {

                    if (accountId == null) {
                        sendEffect(DetailAccountEffect.ShowToast(uiText(R.string.acct_account_not_loaded)))
                        return@launch
                    }

                    sendEffect(
                        DetailAccountEffect.NavigateToAddTransaction(assetId = accountId),
                    )
                }
            }

            DetailAccountEvent.OnEditAccountClick -> {


                viewModelScope.launch {
                    if (accountId == null) {
                        sendEffect(DetailAccountEffect.ShowToast(uiText(R.string.acct_account_not_loaded)))
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
                sendEffect(DetailAccountEffect.ShowToast(uiText(R.string.acct_account_not_loaded)))
                return@launch
            }
            getAccountDetailUseCase(accountId = accountId)
                    .catch { e ->
                        // Wajib: tanpa catch kegagalan Room bocor ke viewModelScope = crash.
                        // Gagal baca mengisi errorMessage; layar menampilkan pesan +
                        // tombol coba lagi, bukan daftar kosong diam.
                        updateState {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.toUiText(R.string.acct_load_failed),
                            )
                        }
                    }
                    .collect { detailData ->
                        updateState {
                            it.copy(
                                isLoading = false,
                                assetId = detailData.account.id,
                                assetName = detailData.account.name,
                                typeAccount = detailData.account.accountType,
                                providers = detailData.account.providerAccount,
                                balance = detailData.account.currentBalance,
                                recentTransactions = detailData.recentTransactions.map { transactionWithDetails -> transactionWithDetails.toItemUiModel() },
                                errorMessage = null,
                            )
                        }
                    }
        }
    }


}