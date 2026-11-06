package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption

import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.mapper.getAllTypeAccountItems
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTypeAccountViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var accountRepository: FakeAccountRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal - daftar tipe akun terisi dan tombol lanjut dinonaktifkan`() = runTest(testDispatcher) {
        val viewModel = AddEditTypeAccountViewModel(accountRepository)

        assertEquals(getAllTypeAccountItems().size, viewModel.state.value.listTypeAccounts.size)
        assertFalse(viewModel.state.value.isNextEnabled)
        assertNull(viewModel.state.value.typeAccountSelected)
    }

    @Test
    fun `pilih tipe akun - mengaktifkan tombol lanjut dan menyimpan tipe yang dipilih`() = runTest(testDispatcher) {
        val viewModel = AddEditTypeAccountViewModel(accountRepository)

        viewModel.onEvent(AddEditTypeAccountEvent.OnTypeAccountSelected(TypeAccount.BANK))

        assertTrue(viewModel.state.value.isNextEnabled)
        assertEquals(TypeAccount.BANK, viewModel.state.value.typeAccountSelected)
    }

    @Test
    fun `klik lanjut tanpa memilih tipe - tidak mengirim efek navigasi`() = runTest(testDispatcher) {
        val viewModel = AddEditTypeAccountViewModel(accountRepository)
        val effects = mutableListOf<AddEditTypeAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }

        viewModel.onEvent(AddEditTypeAccountEvent.OnNextClick)
        runCurrent()

        assertTrue(effects.isEmpty())
    }

    @Test
    fun `klik lanjut dengan tipe terpilih - mengirim efek NavigateToAddEditInfoAccount`() = runTest(testDispatcher) {
        val viewModel = AddEditTypeAccountViewModel(accountRepository)
        val effects = mutableListOf<AddEditTypeAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }

        viewModel.onEvent(AddEditTypeAccountEvent.OnTypeAccountSelected(TypeAccount.EWALLET))
        viewModel.onEvent(AddEditTypeAccountEvent.OnNextClick)
        runCurrent()

        assertEquals(listOf(AddEditTypeAccountEffect.NavigateToAddEditInfoAccount(TypeAccount.EWALLET)), effects)
    }

    @Test
    fun `klik kembali - mengirim efek NavigateBack`() = runTest(testDispatcher) {
        val viewModel = AddEditTypeAccountViewModel(accountRepository)
        val effects = mutableListOf<AddEditTypeAccountEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }

        viewModel.onEvent(AddEditTypeAccountEvent.OnBackClick)
        runCurrent()

        assertEquals(listOf(AddEditTypeAccountEffect.NavigateBack), effects)
    }
}
