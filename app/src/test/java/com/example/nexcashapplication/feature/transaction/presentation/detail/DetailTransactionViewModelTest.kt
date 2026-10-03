package com.example.nexcashapplication.feature.transaction.presentation.detail

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZoneId
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

/**
 * Unit test [DetailTransactionViewModel] — layar detail transaksi.
 *
 * Yang dijaga:
 * - load: ditemukan → terisi; tidak ada → kembali; query gagal → `.catch`
 *   menurunkan `isLoading` + snackbar (bukan crash).
 * - hapus: dialog dibentuk dari data transaksi, tap konfirmasi kedua di tengah
 *   proses tidak menghapus dua kali, gagal → dialog ditutup + snackbar.
 * - **popBackStack hanya sekali**: null yang muncul akibat delete sendiri tidak
 *   boleh mengirim `NavigateBack` kedua (dulu dua layar ikut kebelakang).
 * - BAGIKAN/SALIN/lampiran punya perilaku nyata, bukan `TODO()`.
 *
 * Route dibaca lewat konstruktor `internal` dengan [Route.TransactionRoute.Detail]
 * langsung; konstruktor produksi `@Inject` tetap memakai `SavedStateHandle.toRoute()`
 * yang tidak bisa jalan di JVM unit test (Bundle Android di-mock).
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; semua coroutine &
 * flow fake berjalan di scheduler virtual yang sama.
 *
 * Flush memakai `runCurrent()`, bukan `advanceUntilIdle()`: yang terakhir tidak
 * menjalankan coroutine dari `backgroundScope` (tempat collector effect), jadi
 * daftar effect terus terlihat kosong.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DetailTransactionViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repository = FakeTransactionRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Load ───────────────────────────────────────────────────────────

    @Test
    fun `load - transaksi ditemukan tampil di state tanpa navigasi`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel(sampleDetail())

            assertFalse(vm.state.value.isLoading)
            assertEquals("Kopi pagi", vm.state.value.transaction?.name)
            assertEquals("t1", vm.state.value.transaction?.transactionId)
            assertEquals(emptyList<DetailTransactionEffect>(), effects)
        }

    @Test
    fun `load - id tidak ditemukan langsung kembali ke layar sebelumnya`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel()

            assertNull(vm.state.value.transaction)
            assertEquals(listOf(DetailTransactionEffect.NavigateBack), effects)
        }

    @Test
    fun `load - query gagal menurunkan loading dan memberi pesan`() =
        runTest(testDispatcher) {
            repository.failLoadById = true

            val (vm, effects) = createViewModel()

            assertFalse(vm.state.value.isLoading)
            assertNull(vm.state.value.transaction)
            assertEquals(
                "Transaksi gagal dimuat",
                (effects.single() as DetailTransactionEffect.ShowSnackbar).message,
            )
        }

    // ── Hapus ──────────────────────────────────────────────────────────

    @Test
    fun `hapus - dialog konfirmasi dibentuk dari data transaksi`() =
        runTest(testDispatcher) {
            val (vm, _) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnDeleteClick)
            runCurrent()

            val dialog = vm.state.value.deleteDialogState
            assertEquals("Kopi pagi", dialog?.title)
            assertEquals("EXPENSE", dialog?.typeLabel)
            assertEquals("BCA", dialog?.accountName)
            assertEquals(150_000L.formatCurrency(), dialog?.formattedAmount)
            assertTrue(
                dialog!!.balanceEffectText.contains("bertambah"),
                "pengeluaran dihapus → saldo bertambah: ${dialog.balanceEffectText}",
            )
        }

    @Test
    fun `hapus - sukses menghapus lalu kembali tepat satu kali`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnDeleteConfirm)
            runCurrent()

            assertEquals(listOf("t1"), repository.deletedIds)
            assertEquals(
                listOf(DetailTransactionEffect.NavigateBack),
                effects,
                "null akibat delete sendiri tidak boleh memicu popBackStack kedua",
            )
        }

    @Test
    fun `hapus - tap konfirmasi kedua di tengah proses tidak menghapus dua kali`() =
        runTest(testDispatcher) {
            val gate = CompletableDeferred<Unit>()
            repository.deleteGate = gate
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnDeleteConfirm)
            runCurrent()
            assertTrue(vm.state.value.isDeleting)

            // proses masih tertahan gate → ketuk konfirmasi sekali lagi
            vm.onEvent(DetailTransactionEvent.OnDeleteConfirm)
            runCurrent()

            gate.complete(Unit)
            runCurrent()

            assertEquals(listOf("t1"), repository.deletedIds)
            assertEquals(listOf(DetailTransactionEffect.NavigateBack), effects)
        }

    @Test
    fun `hapus - gagal menutup dialog, membatalkan isDeleting, dan memberi pesan`() =
        runTest(testDispatcher) {
            repository.failDelete = true
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnDeleteClick)
            runCurrent()
            vm.onEvent(DetailTransactionEvent.OnDeleteConfirm)
            runCurrent()

            assertNull(vm.state.value.deleteDialogState, "dialog ditutup supaya bisa dicoba lagi")
            assertFalse(vm.state.value.isDeleting)
            assertEquals(
                "Transaksi gagal dihapus. Coba lagi.",
                (effects.single() as DetailTransactionEffect.ShowSnackbar).message,
            )
            assertEquals(emptyList<String>(), repository.deletedIds)
        }

    // ── Aksi bar ───────────────────────────────────────────────────────

    @Test
    fun `bagikan - mengirim ringkasan transaksi lewat share text`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnShareClick)
            runCurrent()

            val share = (effects.single() as DetailTransactionEffect.ShareTransaction).shareText
            assertTrue(share.contains("Kopi pagi"), share)
            assertTrue(share.contains(150_000L.formatCurrency()), share)
            assertTrue(share.contains("Makanan"), share)
            assertTrue(share.contains("BCA"), share)
            assertTrue(share.contains("Sumber Dana"), "label sumber dana ikut: $share")
            assertTrue(share.contains("Rapat tim"), share)
        }

    @Test
    fun `salin - menyisipkan salinan tanpa id lalu memberi kabar`() =
        runTest(testDispatcher) {
            val source = sampleDetail()
            val (vm, effects) = createViewModel(source)

            vm.onEvent(DetailTransactionEvent.OnDuplicateClick)
            runCurrent()

            val copy = repository.insertedTransactions.single()
            assertNull(copy.id, "salinan harus id baru (null), bukan menimpa yang lama")
            assertEquals(source.transaction.title, copy.title)
            assertEquals(source.transaction.amount, copy.amount)
            assertEquals(source.transaction.categoryId, copy.categoryId)
            assertEquals(source.transaction.accountId, copy.accountId)
            assertEquals(source.transaction.dateTime, copy.dateTime)
            assertEquals(
                "Transaksi disalin",
                (effects.single() as DetailTransactionEffect.ShowSnackbar).message,
            )
        }

    @Test
    fun `salin - kegagalan memberi pesan dan tidak menyisipkan apa pun`() =
        runTest(testDispatcher) {
            repository.failInsert = true
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnDuplicateClick)
            runCurrent()

            assertEquals(emptyList<Transaction>(), repository.insertedTransactions)
            assertEquals(
                "Transaksi gagal disalin. Coba lagi.",
                (effects.single() as DetailTransactionEffect.ShowSnackbar).message,
            )
        }

    @Test
    fun `lampiran - klik memberi tahu pengguna bahwa pratinjau belum tersedia`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnAttachmentClick)
            runCurrent()

            assertEquals(
                "Pratinjau lampiran belum tersedia",
                (effects.single() as DetailTransactionEffect.ShowSnackbar).message,
            )
        }

    // ── Navigasi ───────────────────────────────────────────────────────

    @Test
    fun `navigasi - kembali dan ubah mengirim effect yang sesuai`() =
        runTest(testDispatcher) {
            val (vm, effects) = createViewModel(sampleDetail())

            vm.onEvent(DetailTransactionEvent.OnEditClick)
            vm.onEvent(DetailTransactionEvent.OnBackClick)
            runCurrent()

            assertEquals(
                listOf(
                    DetailTransactionEffect.NavigateToEdit("t1"),
                    DetailTransactionEffect.NavigateBack,
                ),
                effects,
            )
        }

    // ── Peralatan ──────────────────────────────────────────────────────

    /**
     * Buat VM + kumpulkan effect di `backgroundScope` sejak awal, supaya tidak ada
     * effect yang hilang (channel hanya menampung buffer terbatas).
     */
    private fun TestScope.createViewModel(
        vararg seed: TransactionWithDetails,
        transactionId: String = "t1",
    ): Pair<DetailTransactionViewModel, MutableList<DetailTransactionEffect>> {
        repository.seed(*seed)

        val viewModel = DetailTransactionViewModel(
            route = Route.TransactionRoute.Detail(transactionId = transactionId),
            transactionRepository = repository,
        )

        val effects = mutableListOf<DetailTransactionEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        return viewModel to effects
    }

    /** Baris transaksi lengkap dengan kategori & akun — seperti hasil query Room. */
    private fun sampleDetail(
        at: Instant = instant(2026, 10, 1, 8, 30),
    ) = TransactionWithDetails(
        transaction = Transaction(
            id = "t1",
            title = "Kopi pagi",
            amount = 150_000,
            categoryId = "food",
            description = "Rapat tim",
            dateTime = at,
            typeTransaction = TransactionType.EXPENSE,
            accountId = "bca",
        ),
        category = Category(
            id = "food",
            parentId = null,
            name = "Makanan",
            type = TypeCategory.EXPENSE,
            iconId = "coffee",
            color = "#FF0000",
        ),
        account = AccountModel(
            id = "bca",
            accountType = TypeAccount.BANK,
            name = "BCA",
        ),
    )

    /**
     * Instant pada jam lokal tertentu — supaya `formatTime()`/`formatDate()`
     * menghasilkan jam/tanggal yang sama berapa pun zona sistem test.
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
