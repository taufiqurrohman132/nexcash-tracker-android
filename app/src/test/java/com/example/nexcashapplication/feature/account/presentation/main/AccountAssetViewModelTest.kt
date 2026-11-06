package com.example.nexcashapplication.feature.account.presentation.main

import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountAssetViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var getMonthlySummaryUseCase: GetMonthlySummaryUseCase

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        AccountAssetViewModel.lastSnapshot = null
        accountRepository = FakeAccountRepository()
        getMonthlySummaryUseCase = GetMonthlySummaryUseCase(accountRepository)
    }

    @AfterEach
    fun tearDown() {
        AccountAssetViewModel.lastSnapshot = null
        Dispatchers.resetMain()
    }

    @Test
    fun `muat aset - mengisi ringkasan bulanan dan mengelompokkan akun`() = runTest(testDispatcher) {
        accountRepository.monthlySummary.value = MonthlySummaryAccount(
            totalBalance = 3_000_000L,
            activeAccount = 2,
            monthlyIncome = 1_000_000L,
            monthlyExpense = 500_000L,
        )
        accountRepository.seed(
            AccountModel(id = "b1", name = "BCA", currentBalance = 2_000_000L, accountType = TypeAccount.BANK),
            AccountModel(id = "w1", name = "GoPay", currentBalance = 1_000_000L, accountType = TypeAccount.EWALLET),
        )

        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        awaitSettled(state = { viewModel.state.value }, isLoading = { it.isLoading })

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(3_000_000L, state.totalBalance)
        assertEquals(2, state.activeAssetCount)
        assertEquals(2, state.bankAccounts.size)
    }

    @Test
    fun `toggle visibilitas saldo - mengubah nilai isBalanceVisible`() = runTest(testDispatcher) {
        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        awaitSettled(state = { viewModel.state.value }, isLoading = { it.isLoading })

        val initialVisibility = viewModel.state.value.isBalanceVisible

        viewModel.onEvent(AccountAssetEvent.OnToggleBalanceVisibility)
        runCurrent()

        assertEquals(!initialVisibility, viewModel.state.value.isBalanceVisible)
    }

    @Test
    fun `klik item akun - mengirim efek NavigateToAccountDetail`() = runTest(testDispatcher) {
        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        val effects = mutableListOf<AccountAssetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }

        viewModel.onEvent(AccountAssetEvent.OnAccountClick("acc_123"))
        runCurrent()

        assertEquals(listOf(AccountAssetEffect.NavigateToAccountDetail("acc_123")), effects)
    }

    @Test
    fun `klik tambah akun - mengirim efek NavigateToAddAccount`() = runTest(testDispatcher) {
        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        val effects = mutableListOf<AccountAssetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }

        viewModel.onEvent(AccountAssetEvent.OnAddAccountClick)
        runCurrent()

        assertEquals(listOf(AccountAssetEffect.NavigateToAddAccount), effects)
    }

    @Test
    fun `gagal memuat akun - memperbarui error dan mengirim efek ShowError`() = runTest(testDispatcher) {
        accountRepository.failGetAll = true
        val effects = mutableListOf<AccountAssetEffect>()

        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        awaitSettled(state = { viewModel.state.value }, isLoading = { it.isLoading })
        runCurrent()

        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.error)
        assertTrue(effects.any { it is AccountAssetEffect.ShowError })
    }

    @Test
    fun `retry setelah gagal - mereset error dan memuat ulang data`() = runTest(testDispatcher) {
        accountRepository.failGetAll = true
        val viewModel = AccountAssetViewModel(getMonthlySummaryUseCase, accountRepository)
        awaitSettled(state = { viewModel.state.value }, isLoading = { it.isLoading })

        // Sembuhkan repository dan lakukan retry
        accountRepository.failGetAll = false
        accountRepository.seed(AccountModel(id = "b1", name = "BCA", accountType = TypeAccount.BANK))

        viewModel.onEvent(AccountAssetEvent.OnRetry)
        awaitSettled(state = { viewModel.state.value }, isLoading = { it.isLoading })

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.error)
        assertEquals(1, viewModel.state.value.bankAccounts.size)
    }
}
