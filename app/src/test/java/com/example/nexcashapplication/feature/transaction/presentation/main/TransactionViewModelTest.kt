package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.R
import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetAllCategoryUsecase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsWithDetailsByMonthUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
import java.time.YearMonth
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var categoryRepository: FakeCategoryRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        transactionRepository = FakeTransactionRepository()
        categoryRepository = FakeCategoryRepository()
        resetSnapshot()
    }

    @AfterEach
    fun tearDown() {
        resetSnapshot()
        Dispatchers.resetMain()
    }

    private fun resetSnapshot() {
        runCatching {
            val clazz = Class.forName("com.example.nexcashapplication.feature.transaction.presentation.main.TransactionViewModelKt")
            val field = clazz.getDeclaredField("lastSnapshot")
            field.isAccessible = true
            field.set(null, null)
        }
    }

    private fun createViewModel(): TransactionViewModel {
        return TransactionViewModel(
            getTransactionsWithDetailsByMonthUseCase = GetTransactionsWithDetailsByMonthUseCase(transactionRepository),
            getMonthlySummaryUseCase = GetMonthlySummaryUseCase(transactionRepository),
            getAllCategoryUsecase = GetAllCategoryUsecase(categoryRepository),
            transactionRepository = transactionRepository,
        )
    }

    private fun awaitSearchSettled(
        vm: TransactionViewModel,
        condition: (List<TransactionItemUiModel>) -> Boolean,
        timeoutMs: Long = 3_000,
    ) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            testDispatcher.scheduler.advanceUntilIdle()
            if (condition(vm.state.value.trasactionsSearchList)) return
            Thread.sleep(10)
        }
    }

    @Test
    fun `initial load - memuat ringkasan bulanan, kategori, dan transaksi terkelompok`() =
        runTest(testDispatcher) {
            val cat = Category(
                id = "cat-1",
                name = "Makanan",
                type = TypeCategory.EXPENSE,
                iconId = "coffee",
                color = "#FF0000",
            )
            categoryRepository.seed(cat)

            val summary = MonthlySummary(
                totalPemasukan = 5_000_000L,
                totalPengeluaran = 2_000_000L,
                totalSaldo = 3_000_000L,
                yearMonth = YearMonth.now().toString(),
            )
            transactionRepository.seedMonthlySummary(summary)

            val tx = sampleTransaction(
                id = "tx-1",
                categoryId = "cat-1",
                title = "Makan Siang",
                at = instant(2026, 11, 9, 12, 0),
            )
            transactionRepository.seed(tx)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertFalse(vm.state.value.isLoading)
            assertNull(vm.state.value.errorMessage)
            assertEquals(3_000_000L, vm.state.value.todayTotal)
            assertEquals(3_000_000L, vm.state.value.weekTotal)
            assertEquals(3_000_000L, vm.state.value.remainingBudget)

            assertEquals(1, vm.state.value.categoryList.size)
            assertEquals("Makanan", vm.state.value.categoryList[0]?.nameCategory)

            assertEquals(1, vm.state.value.groupedTransactions.size)
            assertEquals("tx-1", vm.state.value.groupedTransactions[0].listTransaction[0].id)
        }

    @Test
    fun `initial load - kegagalan query bulanan ditangkap catch dan mengisi errorMessage`() =
        runTest(testDispatcher) {
            transactionRepository.failObserverTransactionsWithDetailsByMonth = true

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertFalse(vm.state.value.isLoading)
            assertNotNull(vm.state.value.errorMessage)
        }

    @Test
    fun `OnCategorySelected - memperbarui selectedCategoryId di state dan memfilter daftar transaksi`() =
        runTest(testDispatcher) {
            val cat1 = Category(id = "c1", name = "Makanan", type = TypeCategory.EXPENSE, iconId = "coffee", color = "#FF0000")
            val cat2 = Category(id = "c2", name = "Transport", type = TypeCategory.EXPENSE, iconId = "car", color = "#0000FF")
            categoryRepository.seed(cat1, cat2)

            val tx1 = sampleTransaction(id = "tx-1", categoryId = "c1", title = "Kopi")
            val tx2 = sampleTransaction(id = "tx-2", categoryId = "c2", title = "Bensin")
            transactionRepository.seed(tx1, tx2)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals(2, vm.state.value.groupedTransactions.sumOf { it.listTransaction.size })

            vm.onEvent(TransactionEvent.OnCategorySelected("c1"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("c1", vm.state.value.selectedCategoryId)
            val filteredTransactions = vm.state.value.groupedTransactions.flatMap { it.listTransaction }
            assertEquals(1, filteredTransactions.size)
            assertEquals("tx-1", filteredTransactions[0].id)
        }

    @Test
    fun `OnOpenMonthPicker dan OnMonthPickerDismiss mengubah status dialog picker bulan`() =
        runTest(testDispatcher) {
            val vm = createViewModel()

            assertFalse(vm.state.value.showMonthPicker)

            vm.onEvent(TransactionEvent.OnOpenMonthPicker)
            assertTrue(vm.state.value.showMonthPicker)

            vm.onEvent(TransactionEvent.OnMonthPickerDismiss)
            assertFalse(vm.state.value.showMonthPicker)
        }

    @Test
    fun `OnMonthSelected - mengubah selectedMonth dan menutup picker`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            vm.onEvent(TransactionEvent.OnOpenMonthPicker)
            assertTrue(vm.state.value.showMonthPicker)

            val nextMonth = YearMonth.of(2027, 1)
            vm.onEvent(TransactionEvent.OnMonthSelected(nextMonth))

            assertEquals(nextMonth, vm.state.value.selectedMonth)
            assertFalse(vm.state.value.showMonthPicker)
        }

    @Test
    fun `OnGroupToggle - membuka dan melipat grup tanggal`() =
        runTest(testDispatcher) {
            val vm = createViewModel()

            assertTrue(vm.state.value.collapsGroups.isEmpty())

            vm.onEvent(TransactionEvent.OnGroupToggle("2026-11-09"))
            assertTrue(vm.state.value.collapsGroups.contains("2026-11-09"))

            vm.onEvent(TransactionEvent.OnGroupToggle("2026-11-09"))
            assertFalse(vm.state.value.collapsGroups.contains("2026-11-09"))
        }

    @Test
    fun `OnSearchQueryChange dan OnSearchClear - mencari transaksi dan membersihkan hasil pencarian`() =
        runTest(testDispatcher) {
            val tx1 = sampleTransaction(id = "tx-1", title = "Kopi Kenangan")
            val tx2 = sampleTransaction(id = "tx-2", title = "Nasi Padang")
            transactionRepository.seed(tx1, tx2)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(TransactionEvent.OnSearchQueryChange("kopi"))
            assertEquals("kopi", vm.state.value.searchQuery)

            advanceTimeBy(350.milliseconds)
            awaitSearchSettled(vm, condition = { it.isNotEmpty() })

            assertEquals(1, vm.state.value.trasactionsSearchList.size)
            assertEquals("tx-1", vm.state.value.trasactionsSearchList[0].id)

            vm.onEvent(TransactionEvent.OnSearchClear)
            assertEquals("", vm.state.value.searchQuery)

            advanceTimeBy(350.milliseconds)
            awaitSearchSettled(vm, condition = { it.isEmpty() })
            assertTrue(vm.state.value.trasactionsSearchList.isEmpty())
        }

    @Test
    fun `OnSearchQueryChange - saat query spasi kosong mengembalikan daftar kosong`() =
        runTest(testDispatcher) {
            val tx1 = sampleTransaction(id = "tx-1", title = "Kopi")
            transactionRepository.seed(tx1)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(TransactionEvent.OnSearchQueryChange("   "))
            advanceTimeBy(350.milliseconds)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(vm.state.value.trasactionsSearchList.isEmpty())
        }

    @Test
    fun `OnSearchQueryChange - kegagalan search ditangkap catch dan mengosongkan list pencarian`() =
        runTest(testDispatcher) {
            transactionRepository.failSearch = true

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(TransactionEvent.OnSearchQueryChange("kopi"))
            advanceTimeBy(350.milliseconds)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(vm.state.value.trasactionsSearchList.isEmpty())
        }

    @Test
    fun `OnAddTransactionClick - mengirim efek NavigateToAddTransaction`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = mutableListOf<TransactionEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }
            runCurrent()

            vm.onEvent(TransactionEvent.OnAddTransactionClick)
            runCurrent()

            assertEquals(listOf(TransactionEffect.NavigateToAddTransaction), effects)
        }

    @Test
    fun `OnTransactionClick - dengan id valid mengirim NavigateToDetail`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = mutableListOf<TransactionEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }
            runCurrent()

            vm.onEvent(TransactionEvent.OnTransactionClick("tx-123"))
            runCurrent()

            assertEquals(listOf(TransactionEffect.NavigateToDetail("tx-123")), effects)
        }

    @Test
    fun `OnTransactionClick - dengan id null mengirim ShowSnackbar peringatan`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = mutableListOf<TransactionEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }
            runCurrent()

            vm.onEvent(TransactionEvent.OnTransactionClick(null))
            runCurrent()

            assertEquals(1, effects.size)
            assertTrue(effects[0] is TransactionEffect.ShowSnackbar)
            val effect = effects[0] as TransactionEffect.ShowSnackbar
            assertEquals(uiText(R.string.tx_transaction_unavailable), effect.message)
        }

    @Test
    fun `restoreSnapshot - bila snapshot tersimpan untuk bulan yang sama, langsung digunakan di awal`() =
        runTest(testDispatcher) {
            val cat = Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, iconId = "coffee", color = "#FF0000")
            categoryRepository.seed(cat)
            val tx = sampleTransaction(id = "tx-1", categoryId = "cat-1", title = "Makan Siang")
            transactionRepository.seed(tx)

            // Buat instance pertama agar menghasilkan lastSnapshot
            val vm1 = createViewModel()
            awaitSettled(state = { vm1.state.value }, isLoading = { it.isLoading })
            assertFalse(vm1.state.value.groupedTransactions.isEmpty())

            // Buat instance kedua tanpa mereset snapshot
            val vm2 = createViewModel()
            // State awal langsung memiliki groupedTransactions dari snapshot dan isLoading false
            assertFalse(vm2.state.value.isLoading)
            assertFalse(vm2.state.value.groupedTransactions.isEmpty())
        }

    private fun sampleTransaction(
        id: String,
        title: String = "Test Transaction",
        categoryId: String = "c1",
        accountId: String = "a1",
        amount: Long = 50_000L,
        at: Instant = instant(2026, 11, 9, 10, 0),
    ): TransactionWithDetails {
        return TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = title,
                amount = amount,
                categoryId = categoryId,
                description = "Deskripsi $title",
                dateTime = at,
                typeTransaction = TypeCategory.EXPENSE,
                accountId = accountId,
            ),
            category = Category(
                id = categoryId,
                name = "Kategori",
                type = TypeCategory.EXPENSE,
                iconId = "coffee",
                color = "#FF0000",
            ),
            account = AccountModel(
                id = accountId,
                name = "Akun",
                accountType = TypeAccount.BANK,
                currentBalance = 1_000_000L,
            ),
        )
    }

    private fun instant(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Instant =
        java.time.ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault())
            .toInstant()
            .toKotlinInstant()
}
