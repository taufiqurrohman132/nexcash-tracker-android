package com.example.nexcashapplication.feature.account.presentation.addedit.form

import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.usecase.GetAccountDetailUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.SaveAccontUseCase
import com.example.nexcashapplication.feature.account.domain.usecase.UpdateAccontUseCase
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditInfoAccountViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var updateAssetUseCase: UpdateAccontUseCase
    private lateinit var saveAssetUseCase: SaveAccontUseCase
    private lateinit var getAccountDetailUseCase: GetAccountDetailUseCase

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        updateAssetUseCase = UpdateAccontUseCase(accountRepository)
        saveAssetUseCase = SaveAccontUseCase(accountRepository)
        getAccountDetailUseCase = GetAccountDetailUseCase(accountRepository, transactionRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        accountId: String? = null,
        typeAccount: TypeAccount? = TypeAccount.BANK,
    ) = AddEditInfoAccountViewModel(
        route = Route.AccountRoute.AddEditInfo(accountId = accountId, typeAccount = typeAccount),
        updateAssetUseCase = updateAssetUseCase,
        saveAssetUseCase = saveAssetUseCase,
        getAccountDetailUseCase = getAccountDetailUseCase,
    )

    @Test
    fun `mode tambah - inisialisasi tipe akun dan daftar provider dari katalog`() = runTest(testDispatcher) {
        val viewModel = createViewModel(typeAccount = TypeAccount.BANK)
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isEditMode)
        assertEquals(TypeAccount.BANK, state.typeAccount)
        assertEquals(ProviderCatalog.getByType(TypeAccount.BANK).size, state.providers.size)
    }

    @Test
    fun `mode edit - memuat data awal akun yang akan diedit`() = runTest(testDispatcher) {
        val bcaProvider = ProviderCatalog.getById("bca")
        accountRepository.seed(
            AccountModel(
                id = "acc1",
                name = "Tabungan BCA",
                accountType = TypeAccount.BANK,
                providerAccount = bcaProvider,
                currentBalance = 750_000L,
            ),
        )

        val viewModel = createViewModel(accountId = "acc1")
        runCurrent()

        val state = viewModel.state.value
        assertTrue(state.isEditMode)
        assertEquals("acc1", state.accountId)
        assertEquals("Tabungan BCA", state.namaAset)
        assertEquals("bca", state.selectedProviderId)
        assertEquals(750_000L, state.saldoAwal)
    }

    @Test
    fun `validasi simpan - nama aset kosong memunculkan pesan error`() = runTest(testDispatcher) {
        val viewModel = createViewModel(typeAccount = TypeAccount.BANK)
        runCurrent()

        viewModel.onEvent(AddEditInfoAccountEvent.SimpanClicked)
        runCurrent()

        assertNotNull(viewModel.state.value.namaAsetError)
        assertTrue(accountRepository.insertedAccounts.isEmpty())
    }

    @Test
    fun `mode tambah simpan sukses - menyisipkan akun dan mengirim efek AssetSavedNavigateMain`() = runTest(testDispatcher) {
        val viewModel = createViewModel(typeAccount = TypeAccount.BANK)
        val effects = mutableListOf<AddEditInfoAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditInfoAccountEvent.NamaAsetChanged("BCA Baru"))
        viewModel.onEvent(AddEditInfoAccountEvent.SaldoAwalChanged("500000"))
        viewModel.onEvent(AddEditInfoAccountEvent.ProviderSelected("bca"))
        viewModel.onEvent(AddEditInfoAccountEvent.SimpanClicked)
        runCurrent()

        assertEquals(1, accountRepository.insertedAccounts.size)
        assertEquals("BCA Baru", accountRepository.insertedAccounts[0].name)
        assertEquals(500_000L, accountRepository.insertedAccounts[0].currentBalance)
        assertTrue(viewModel.state.value.isSaving)
        assertEquals(listOf(AddEditInfoAccountEffect.AssetSavedNavigateMain), effects)
    }

    @Test
    fun `mode edit simpan sukses - memperbarui akun dan mengirim efek NavigateBack`() = runTest(testDispatcher) {
        accountRepository.seed(
            AccountModel(id = "acc1", name = "BCA Lama", accountType = TypeAccount.BANK, currentBalance = 100_000L),
        )

        val viewModel = createViewModel(accountId = "acc1")
        val effects = mutableListOf<AddEditInfoAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditInfoAccountEvent.NamaAsetChanged("BCA Diedit"))
        viewModel.onEvent(AddEditInfoAccountEvent.SaldoAwalChanged("200000"))
        viewModel.onEvent(AddEditInfoAccountEvent.SimpanClicked)
        runCurrent()

        assertEquals(1, accountRepository.updatedAccounts.size)
        assertEquals("BCA Diedit", accountRepository.updatedAccounts[0].name)
        assertEquals(200_000L, accountRepository.updatedAccounts[0].currentBalance)
        assertTrue(viewModel.state.value.isSaving)
        assertEquals(listOf(AddEditInfoAccountEffect.NavigateBack), effects)
    }

    @Test
    fun `simpan gagal - menampilkan efek ShowError`() = runTest(testDispatcher) {
        accountRepository.failInsert = true

        val viewModel = createViewModel(typeAccount = TypeAccount.BANK)
        val effects = mutableListOf<AddEditInfoAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditInfoAccountEvent.NamaAsetChanged("Bank Gagal"))
        viewModel.onEvent(AddEditInfoAccountEvent.SimpanClicked)
        runCurrent()

        assertTrue(effects.any { it is AddEditInfoAccountEffect.ShowError })
    }

    @Test
    fun `navigasi - klik kembali dan klik lihat semua provider`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val effects = mutableListOf<AddEditInfoAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditInfoAccountEvent.BackClicked)
        viewModel.onEvent(AddEditInfoAccountEvent.LihatSemuaProviderClicked)
        runCurrent()

        assertEquals(
            listOf(
                AddEditInfoAccountEffect.NavigateBack,
                AddEditInfoAccountEffect.NavigateToProviderList,
            ),
            effects,
        )
    }
}
