package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.awaitSettled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

/**
 * Unit test [CurrencySettingsViewModel].
 *
 * Regresi utamanya: state dan ViewModel dulu masih berbahasa — `listLanguage`,
 * `selectedLanguageId`, tanpa dependency repository, dan empat `TODO()` di
 * `OnBackClick`/`OnSaveClick`/`OnSearchClear`/`OnSearchQueryChange`.
 *
 * Dispatcher: `Dispatchers.Main` diganti [StandardTestDispatcher] supaya semua
 * coroutine ViewModel berjalan di scheduler virtual yang sama.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CurrencySettingsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var settingsRepository: FakeSettingsRepository

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        settingsRepository = FakeSettingsRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── State terisi ─────────────────────────────────────────────────

    @Test
    fun `daftar mata uang terisi - bukan daftar bahasa`() = runTest(testDispatcher) {
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        val state = vm.state.value
        assertEquals(
            listOf("IDR", "USD", "EUR", "SGD", "JPY", "MYR", "GBP", "AUD"),
            state.listCurrency.map { it.id },
        )
        assertEquals("Rupiah Indonesia", state.listCurrency.first().name)
        assertEquals("Rp", state.listCurrency.first().symbol)
    }

    @Test
    fun `pilihan awal mengikuti preferensi tersimpan - tombol Simpan mati`() =
        runTest(testDispatcher) {
            settingsRepository = FakeSettingsRepository(initialCurrency = "USD")

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val state = vm.state.value
            assertEquals("USD", state.selectedCurrencyId)
            assertEquals("USD", state.initialSelectedCurrencyId)
            assertFalse(state.hasChange)
        }

    @Test
    fun `kode tersimpan tak dikenal - jatuh ke IDR`() = runTest(testDispatcher) {
        settingsRepository = FakeSettingsRepository(initialCurrency = "XXX")

        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals("IDR", vm.state.value.selectedCurrencyId)
    }

    // ── Interaksi ────────────────────────────────────────────────────

    @Test
    fun `OnItemClick - mengubah pilihan dan menyalakan tombol Simpan`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(CurrencySettingsEvent.OnItemClick("EUR"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("EUR", vm.state.value.selectedCurrencyId)
            assertTrue(vm.state.value.hasChange)
        }

    @Test
    fun `OnSaveClick - menyimpan pilihan lalu kembali`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(CurrencySettingsEvent.OnItemClick("EUR"))
        vm.onEvent(CurrencySettingsEvent.OnSaveClick)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals("EUR", settingsRepository.savedCurrency)
        assertEquals(
            listOf<CurrencySettingsEffect>(CurrencySettingsEffect.NavigateBack),
            effects,
        )
        // preferensi tersimpan ikut terbalik → penanda "berubah" ikut bersih
        assertFalse(vm.state.value.hasChange)
    }

    @Test
    fun `OnBackClick - kembali tanpa menyimpan`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(CurrencySettingsEvent.OnItemClick("EUR"))
        vm.onEvent(CurrencySettingsEvent.OnBackClick)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertNull(settingsRepository.savedCurrency)
        assertEquals(
            listOf<CurrencySettingsEffect>(CurrencySettingsEffect.NavigateBack),
            effects,
        )
        // pilihan yang belum disimpan tidak boleh diam-diam ikut ter-reset
        assertEquals("EUR", vm.state.value.selectedCurrencyId)
    }

    // ── Pencarian ────────────────────────────────────────────────────

    @Test
    fun `pencarian - filter daftar mengikuti nama`() = runTest(testDispatcher) {
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })
        assertEquals(8, vm.state.value.listCurrency.size)

        vm.onEvent(CurrencySettingsEvent.OnSearchQueryChange("dolar"))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals("dolar", vm.state.value.searchQuery)
        assertEquals(
            listOf("USD", "SGD", "AUD"),
            vm.state.value.listCurrency.map { it.id },
        )
    }

    @Test
    fun `pencarian - kode ISO juga dicari`() = runTest(testDispatcher) {
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(CurrencySettingsEvent.OnSearchQueryChange("jpy"))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals(listOf("JPY"), vm.state.value.listCurrency.map { it.id })
    }

    @Test
    fun `OnSearchClear - mengosongkan query dan mengembalikan daftar penuh`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(CurrencySettingsEvent.OnSearchQueryChange("dolar"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })
            assertEquals(3, vm.state.value.listCurrency.size)

            vm.onEvent(CurrencySettingsEvent.OnSearchClear)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("", vm.state.value.searchQuery)
            assertEquals(8, vm.state.value.listCurrency.size)
        }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun createViewModel() = CurrencySettingsViewModel(settingsRepository)

    /** Kumpulkan semua effect selama test berjalan (collector di background scope). */
    private fun TestScope.collectEffects(
        vm: CurrencySettingsViewModel,
    ): MutableList<CurrencySettingsEffect> {
        val effects = mutableListOf<CurrencySettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }
}
