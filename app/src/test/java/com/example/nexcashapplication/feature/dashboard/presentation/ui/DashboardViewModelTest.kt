package com.example.nexcashapplication.feature.dashboard.presentation.ui

import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Unit test [DashboardViewModel]:
 * - load "transaksi terakhir" tanpa filter kategori (`getRecentTransactions`)
 *   dibatasi limit; regresi lama: dipanggil lewat `getRecentTransactionsByCategory`
 *   dengan `""` → WHERE category_id = '' tidak pernah cocok → daftar selalu kosong
 * - toggle visibilitas saldo
 * - SEMUA event navigasi mengirim effect — regresi lama: `TODO()` = crash saat ditekan
 * - OnRefresh/OnScreenLoad reload tanpa menumpuk collector ganda
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; semua coroutine
 * ViewModel berjalan di scheduler virtual yang sama.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var transactionRepository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        transactionRepository = FakeTransactionRepository()
        // cache lintas ViewModel bersifat statis — kalau tidak di-reset, test
        // ini langsung kebagian data test sebelumnya
        DashboardViewModel.lastSnapshot = null
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Load ─────────────────────────────────────────────────────────────

    @Test
    fun `transaksi terakhir - semua kategori ikut, urut terbaru, dibatasi limit 5`() =
        runTest(testDispatcher) {
            // 6 transaksi berselang-seling antar kategori (c1 & c2)
            for (hour in 1..6) {
                transactionRepository.seedWithDetails(
                    withDetails(
                        id = "t$hour",
                        catId = if (hour % 2 == 1) "c1" else "c2",
                        hoursAgo = hour.toLong(),
                    ),
                )
            }

            val vm = createViewModel()
            // hasil mapping sampai lewat Dispatchers.Default → tunggu state mengendap
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            // Kategori c2 ikut terbaca → membuktikan daftar tanpa filter kategori;
            // kode lama memakai ByCategory("") sehingga daftar ini selalu kosong
            val ids = vm.state.value.transactions.map { it.id }
            assertEquals(listOf("t1", "t2", "t3", "t4", "t5"), ids)
        }

    @Test
    fun `transaksi terakhir - tanpa data daftar kosong tanpa crash`() = runTest(testDispatcher) {
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertTrue(vm.state.value.transactions.isEmpty())
    }

    // ── State ────────────────────────────────────────────────────────────

    @Test
    fun `OnToggleBalanceVisibility - membalik visibilitas saldo`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        val awal = vm.state.value.isBalanceVisible
        vm.onEvent(DashboardEvent.OnToggleBalanceVisibility)
        assertFalse(vm.state.value.isBalanceVisible == awal)
        vm.onEvent(DashboardEvent.OnToggleBalanceVisibility)
        assertEquals(awal, vm.state.value.isBalanceVisible)
    }

    // ── Event → effect (regresi TODO crash) ──────────────────────────────

    @Test
    fun `semua event navigasi - mengirim effect yang sesuai, tidak TODO crash`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DashboardEvent.OnNotificationClick)
            vm.onEvent(DashboardEvent.OnProfileClick)
            vm.onEvent(DashboardEvent.OnKategoriClick)
            vm.onEvent(DashboardEvent.OnTambahAkunClick)
            vm.onEvent(DashboardEvent.OnStatistikClick)
            vm.onEvent(DashboardEvent.OnSemuaTransaksiClick)
            vm.onEvent(DashboardEvent.OnAddTransactionClick)
            vm.onEvent(DashboardEvent.OnLihatDetailBudgetClick)
            vm.onEvent(DashboardEvent.OnTransactionClick("tx-9"))
            advanceUntilIdle()

            assertEquals(
                listOf<DashboardEffect>(
                    DashboardEffect.NavigateToNotifications,
                    DashboardEffect.NavigateToProfile,
                    DashboardEffect.NavigateToCategories,
                    DashboardEffect.NavigateToAddAccount,
                    DashboardEffect.NavigateToStatistics,
                    DashboardEffect.NavigateToAllTransactions,
                    DashboardEffect.NavigateToAddTransaction,
                    DashboardEffect.NavigateToBudgetDetail,
                    DashboardEffect.NavigateToTransactionDetail("tx-9"),
                ),
                effects,
            )
        }

    @Test
    fun `OnRefresh dan OnScreenLoad - reload tanpa crash dan tanpa duplikasi`() =
        runTest(testDispatcher) {
            transactionRepository.seedWithDetails(withDetails("t1", "c1", hoursAgo = 1))

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(DashboardEvent.OnRefresh)
            vm.onEvent(DashboardEvent.OnScreenLoad)
            // reload menyalakan isLoading lagi → tunggu emisi berikutnya
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            // collector lama dibatalkan saat reload → daftar tidak terduplikasi
            assertEquals(listOf("t1"), vm.state.value.transactions.map { it.id })
        }

    // ── Helpers ──────────────────────────────────────────────────────────

    /** Kumpulkan semua effect selama test berjalan (collector di background scope). */
    private fun kotlinx.coroutines.test.TestScope.collectEffects(
        vm: DashboardViewModel,
    ): MutableList<DashboardEffect> {
        val effects = mutableListOf<DashboardEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }

    private fun createViewModel() = DashboardViewModel(
        transactionRepository = transactionRepository,
    )

    private fun withDetails(id: String, catId: String, hoursAgo: Long) =
        TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = "tx-$id",
                amount = 25_000,
                categoryId = catId,
                description = null,
                dateTime = Instant.fromEpochMilliseconds(
                    Clock.System.now().toEpochMilliseconds() - hoursAgo * 60 * 60 * 1000,
                ),
                typeTransaction = TransactionType.EXPENSE,
                accountId = "acc1",
            ),
        )
}
