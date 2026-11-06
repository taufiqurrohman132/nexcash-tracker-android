package com.example.nexcashapplication.feature.account.presentation.detail

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.usecase.DeleteAccountUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.GetAccountDetailUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.ValidationDeleteAccountUseCase
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailAccountViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase
    private lateinit var validationDeleteAccountUseCase: ValidationDeleteAccountUseCase
    private lateinit var getAccountDetailUseCase: GetAccountDetailUseCase

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        deleteAccountUseCase = DeleteAccountUseCase(accountRepository)
        validationDeleteAccountUseCase = ValidationDeleteAccountUseCase(accountRepository, transactionRepository)
        getAccountDetailUseCase = GetAccountDetailUseCase(accountRepository, transactionRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun withDetails(id: String, accountId: String, title: String = "tx-$id") =
        TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = title,
                amount = 500_000L,
                categoryId = "c1",
                description = null,
                typeTransaction = TypeCategory.INCOME,
                accountId = accountId,
            ),
        )

    private fun createViewModel(accountId: String = "acc1") = DetailAccountViewModel(
        route = Route.AccountRoute.Detail(accountId),
        deleteAccountUseCase = deleteAccountUseCase,
        validationDeleteAccountUseCase = validationDeleteAccountUseCase,
        getAccountDetailUseCase = getAccountDetailUseCase,
    )

    @Test
    fun `load detail sukses - state terisi nama saldo tipe dan riwayat transaksi`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(
                id = "acc1",
                name = "BCA Tabungan",
                accountType = TypeAccount.BANK,
                currentBalance = 1_500_000L,
            ),
        )
        transactionRepository.seed(
            withDetails("tx1", "acc1", "Transfer Masuk"),
        )

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("acc1", state.assetId)
        assertEquals("BCA Tabungan", state.assetName)
        assertEquals(TypeAccount.BANK, state.typeAccount)
        assertEquals(1_500_000L, state.balance)
        assertEquals(1, state.recentTransactions.size)
        assertEquals("tx1", state.recentTransactions[0].id)
        assertNull(state.errorMessage)
    }

    @Test
    fun `load detail gagal - mengeset pesan error dan mematikan loading`() = runTest(testDispatcher) {
        accountRepository.failGetById = true

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `klik hapus akun - validasi dijalankan dan memunculkan deleteDialogDesision`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(id = "acc1", name = "BCA", currentBalance = 50_000L, accountType = TypeAccount.BANK),
        )

        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(DetailAccountEvent.OnDeleteAccountClick)
        runCurrent()

        assertTrue(viewModel.state.value.deleteDialogDesision is DeleteAccountDesision.Blocked)
    }

    @Test
    fun `konfirmasi hapus - menghapus akun dan mengirim efek NavigateBackAfterDelete`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(id = "acc1", name = "BCA", currentBalance = 0L, accountType = TypeAccount.BANK),
        )

        val viewModel = createViewModel()
        val effects = mutableListOf<DetailAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(DetailAccountEvent.OnConfirmDelete)
        runCurrent()

        assertEquals(listOf("acc1"), accountRepository.deletedAccountIds)
        assertEquals(listOf(DetailAccountEffect.NavigateBackAfterDelete), effects)
    }

    @Test
    fun `tutup dialog hapus - mereset deleteDialogDesision ke null`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(id = "acc1", name = "BCA", currentBalance = 50_000L, accountType = TypeAccount.BANK),
        )

        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(DetailAccountEvent.OnDeleteAccountClick)
        runCurrent()
        assertNotNull(viewModel.state.value.deleteDialogDesision)

        viewModel.onEvent(DetailAccountEvent.OnDismissDeleteDialog)
        runCurrent()
        assertNull(viewModel.state.value.deleteDialogDesision)
    }

    @Test
    fun `navigasi - kembali, lihat semua, tambah transaksi, edit aset, klik transaksi`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(id = "acc1", name = "BCA", currentBalance = 0L, accountType = TypeAccount.BANK),
        )

        val viewModel = createViewModel()
        val effects = mutableListOf<DetailAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(DetailAccountEvent.OnBackClick)
        viewModel.onEvent(DetailAccountEvent.OnLihatSemuaClick)
        viewModel.onEvent(DetailAccountEvent.OnAddTransactionClick)
        viewModel.onEvent(DetailAccountEvent.OnEditAccountClick)
        viewModel.onEvent(DetailAccountEvent.OnTransactionClick("tx99"))
        runCurrent()

        assertEquals(
            listOf(
                DetailAccountEffect.NavigateBack,
                DetailAccountEffect.NavigateToAllTransactions("acc1"),
                DetailAccountEffect.NavigateToAddTransaction("acc1"),
                DetailAccountEffect.NavigateToEditAssetName("acc1"),
                DetailAccountEffect.NavigateToTransactionDetail("tx99"),
            ),
            effects,
        )
    }
}
