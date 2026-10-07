package com.example.nexcashapplication.feature.account.presentation.main

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.account.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.account.presentation.main.mapper.toAccountUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "AssetVm"

@HiltViewModel
class AccountAssetViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val accountRepository: AccountRepository,
) : BaseViewModel<AccountAssetState, AccountAssetEvent, AccountAssetEffect>(initialState = AccountAssetState()) {

    private var summaryJob: Job? = null
    private var accountJob: Job? = null

    // Fase 0 (diagnostik): adb logcat -s AssetVm
    private val createdAt = SystemClock.uptimeMillis()
    private var firstDataLogged = false

    init {
        Log.d(TAG, "VM dibuat ulang")
        restoreSnapshot()
        observeMonthlySummary()
        observeAccount()
    }

    /** Suntik data lama biar frame pertama tidak kosong — lihat [lastSnapshot]. */
    private fun restoreSnapshot() {
        val cached = lastSnapshot ?: return

        updateState {
            cached.copy(
                error = null,
                // belum pernah ada daftar akun → indikator tetap menyala
                isLoading = cached.bankAccounts.isEmpty() && cached.eWallets.isEmpty(),
            )
        }
        Log.d(TAG, "snapshot dipakai → frame pertama langsung terisi")
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
                // beres-beres lalu pasang ulang kedua observer
                updateState { it.copy(error = null, isLoading = true) }
                observeMonthlySummary()
                observeAccount()
            }
            AccountAssetEvent.OnToggleBalanceVisibility -> {
                viewModelScope.launch {
                    updateState { it.copy(isBalanceVisible = !it.isBalanceVisible) }
                }
            }
        }
    }

    private fun observeMonthlySummary() {
        summaryJob?.cancel()
        // Ringkasan bulan-berjalan hanya mengisi angka kartu atas — tidak
        // mematikan isLoading: spinner harus hilang setelah daftar akun masuk,
        // bukan sebelumnya (dulu layar bisa kosong tapi spinner sudah hilang).
        summaryJob = getMonthlySummaryUseCase()
                .onEach { summary ->
                    updateState {
                        it.copy(
                            totalBalance = summary.totalSaldoGabungan,
                            monthlyChangePercent = summary.percentage,
                            activeAssetCount = summary.activeAccount,
                        )
                    }
                }
                .catch { e ->
                    updateState { it.copy(error = e.toUiText(R.string.acct_load_failed)) }
                }
                .launchIn(viewModelScope)
    }

    private fun observeAccount() {
        accountJob?.cancel()
        updateState {
            it.copy(isLoading = true, error = null)
        }

        accountJob = accountRepository.getAllAccounts()
                // group + sort dipindah ke background: ini jalan pas transisi
                // pindah tab ke Asset
                .map { accounts -> accounts.toAccountUiModelList() }
                .flowOn(Dispatchers.Default)
                .catch { throwable ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = throwable.toUiText(R.string.acct_load_failed),
                        )
                    }
                    sendEffect(
                        AccountAssetEffect.ShowError(
                            throwable.toUiText(R.string.acct_load_failed),
                        ),
                    )
                }
                .onEach { groups ->
                    if (!firstDataLogged) {
                        firstDataLogged = true
                        Log.d(
                            TAG,
                            "data pertama (${groups.size} grup) setelah " +
                                    "${SystemClock.uptimeMillis() - createdAt} ms",
                        )
                    }

                    updateState {
                        it.copy(
                            isLoading = false,
                            error = null,
                            bankAccounts = groups,
                        )
                    }
                    lastSnapshot = state.value
                }
                .launchIn(viewModelScope)

    }

    companion object {
        /**
         * Data terakhir yang tampil, disimpan lintas ViewModel (BottomNavBar
         * memakai `saveState`/`restoreState`, jadi VM dibuat ulang tiap kembali
         * ke tab Asset). Statis di level proses — test harus meresetnya antar test.
         */
        internal var lastSnapshot: AccountAssetState? = null
    }
}