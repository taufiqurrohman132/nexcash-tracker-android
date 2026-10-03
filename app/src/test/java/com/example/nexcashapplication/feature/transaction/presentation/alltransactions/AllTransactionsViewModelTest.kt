package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsByFilterUseCase
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZoneId
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

/**
 * Unit test [AllTransactionsViewModel] — halaman "Semua Transaksi" (ADR-002).
 *
 * Satu ViewModel untuk semua titik "Lihat Semua"; filter dibaca dari route dan
 * logika per-varian ada di [GetTransactionsByFilterUseCase]. Yang dijaga:
 * - judul TopAppBar mengikuti filter (tanpa filter → "Semua Transaksi",
 *   kategori → "Transaksi · Nama").
 * - daftar dikelompokkan per tanggal, `isLoading` turun setelah data sampai.
 * - query gagal → `.catch` mengisi `error` + `isLoading` false; `OnRetryClick`
 *   memuat ulang dan menghapus `error`.
 * - lipat grup & effect navigasi.
 *
 * Route dibaca lewat konstruktor `internal` dengan [Route.TransactionRoute.All];
 * konstruktor produksi `@Inject` tetap memakai `SavedStateHandle.toRoute()` yang
 * tidak bisa jalan di JVM unit test (Bundle Android di-mock).
 *
 * Flush memakai `runCurrent()`, bukan `advanceUntilIdle()`: yang terakhir tidak
 * menjalankan coroutine dari `backgroundScope` (tempat collector effect), jadi
 * daftar effect terus terlihat kosong.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AllTransactionsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var accountRepository: FakeAccountRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        transactionRepository = FakeTransactionRepository()
        categoryRepository = FakeCategoryRepository()
        accountRepository = FakeAccountRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Judul ──────────────────────────────────────────────────────────

    @Test
    fun `judul - tanpa filter memakai Semua Transaksi`() = runTest(testDispatcher) {
        val (vm, _) = createViewModel()

        assertEquals("Semua Transaksi", vm.state.value.title)
    }

    @Test
    fun `judul - filter kategori memakai nama kategori dari repository`() =
        runTest(testDispatcher) {
            val (vm, _) = createViewModel(
                route = Route.TransactionRoute.All(categoryId = "food"),
                categories = listOf(food),
            )

            assertEquals("Transaksi · Makanan", vm.state.value.title)
        }

    // ── Daftar ─────────────────────────────────────────────────────────

    @Test
    fun `daftar - dikelompokkan per tanggal dan loading turun`() =
        runTest(testDispatcher) {
            val (vm, _) = createViewModel(
                rows = listOf(
                    transaction(id = "pagi", at = instant(2026, 10, 1, 8, 10)),
                    transaction(id = "sore", at = instant(2026, 10, 1, 17, 40)),
                    transaction(id = "kemarin", at = instant(2026, 9, 30, 9, 0)),
                ),
            )

            val groups = vm.state.value.groupedTransactions
            assertFalse(vm.state.value.isLoading)
            assertNull(vm.state.value.error)
            assertEquals(
                listOf("2026-10-01", "2026-09-30"),
                groups.map { it.header.id },
                "grup mengikuti urutan data dari query — mapper tidak mengurutkan ulang",
            )
            assertEquals(listOf("pagi", "sore"), groups[0].listTransaction.map { it.id })
            assertEquals(listOf("kemarin"), groups[1].listTransaction.map { it.id })
        }

    @Test
    fun `daftar - query gagal menampilkan error dan menurunkan loading`() =
        runTest(testDispatcher) {
            transactionRepository.failGetAllWithDetails = true

            val (vm, _) = createViewModel()

            assertFalse(vm.state.value.isLoading)
            assertNotNull(vm.state.value.error)
            assertTrue(vm.state.value.error!!.contains("query rusak"))
            assertTrue(vm.state.value.groupedTransactions.isEmpty())
        }

    @Test
    fun `daftar - retry memuat ulang dan menghapus error`() = runTest(testDispatcher) {
        transactionRepository.failGetAllWithDetails = true
        val (vm, _) = createViewModel(rows = listOf(transaction(id = "t1")))

        assertNotNull(vm.state.value.error)

        transactionRepository.failGetAllWithDetails = false
        vm.onEvent(AllTransactionsEvent.OnRetryClick)
        runCurrent()

        assertNull(vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf("t1"), vm.state.value.groupedTransactions.single().listTransaction.map { it.id })
    }

    // ── Lipat grup ─────────────────────────────────────────────────────

    @Test
    fun `grup - OnGroupToggle melipat lalu membuka kembali`() = runTest(testDispatcher) {
        val (vm, _) = createViewModel(
            rows = listOf(transaction(id = "t1", at = instant(2026, 10, 1, 8, 10))),
        )
        val groupId = vm.state.value.groupedTransactions.single().header.id

        vm.onEvent(AllTransactionsEvent.OnGroupToggle(groupId))
        runCurrent()
        assertEquals(setOf(groupId), vm.state.value.collapsedGroups)

        vm.onEvent(AllTransactionsEvent.OnGroupToggle(groupId))
        runCurrent()
        assertEquals(emptySet<String>(), vm.state.value.collapsedGroups)
    }

    // ── Navigasi ───────────────────────────────────────────────────────

    @Test
    fun `navigasi - back, detail, dan tambah mengirim effect yang sesuai`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel()

            vm.onEvent(AllTransactionsEvent.OnAddTransactionClick)
            vm.onEvent(AllTransactionsEvent.OnTransactionClick("t9"))
            vm.onEvent(AllTransactionsEvent.OnBackClick)
            runCurrent()

            assertEquals(
                listOf(
                    AllTransactionsEffect.NavigateToAddTransaction,
                    AllTransactionsEffect.NavigateToTransactionDetail("t9"),
                    AllTransactionsEffect.NavigateBack,
                ),
                effects,
            )
        }

    // ── Peralatan ──────────────────────────────────────────────────────

    private fun TestScope.createViewModel(
        route: Route.TransactionRoute.All = Route.TransactionRoute.All(),
        rows: List<TransactionWithDetails> = emptyList(),
        categories: List<Category> = emptyList(),
    ): Pair<AllTransactionsViewModel, MutableList<AllTransactionsEffect>> {
        transactionRepository.seed(*rows.toTypedArray())
        categoryRepository.seed(*categories.toTypedArray())

        val viewModel = AllTransactionsViewModel(
            route = route,
            getTransactionsByFilterUseCase = GetTransactionsByFilterUseCase(
                transactionRepository = transactionRepository,
                categoryRepository = categoryRepository,
                accountRepository = accountRepository,
            ),
        )

        val effects = mutableListOf<AllTransactionsEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        return viewModel to effects
    }

    private val food = Category(
        id = "food",
        parentId = null,
        name = "Makanan",
        type = TypeCategory.EXPENSE,
        iconId = "coffee",
        color = "#FF0000",
    )

    private fun transaction(
        id: String,
        at: Instant = instant(2026, 10, 1, 8, 30),
    ) = TransactionWithDetails(
        transaction = Transaction(
            id = id,
            title = "Transaksi $id",
            amount = 10_000,
            categoryId = "food",
            description = null,
            dateTime = at,
            typeTransaction = TransactionType.EXPENSE,
            accountId = "bca",
        ),
    )

    /**
     * Instant pada jam lokal tertentu — supaya pengelompokan tanggal dan
     * `formatTime()` menghasilkan hasil yang sama berapa pun zona sistem test.
     */
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
