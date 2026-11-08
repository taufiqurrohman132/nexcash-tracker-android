package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.AppLocale
import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import com.example.nexcashapplication.feature.settings.presentation.currency.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageSettingsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var originalLocale: Locale

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        settingsRepository = FakeSettingsRepository()
        originalLocale = Locale.getDefault()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        AppLocale.apply(originalLocale)
    }

    // ── Inisialisasi State ──────────────────────────────────────────

    @Test
    fun `inisialisasi memuat daftar bahasa dan pilihan awal dari repository`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val state = vm.state.value
            assertEquals("id", state.selectedLanguageId)
            assertEquals("id", state.initialSelectedLanguageId)
            assertFalse(state.hasChange)
            assertEquals(AppLanguage.entries.size, state.listLanguage.size)
            assertEquals(
                AppLanguage.entries.map { it.code },
                state.listLanguage.map { it.id },
            )
        }

    @Test
    fun `inisialisasi dengan bahasa non-default tersimpan di repository`() =
        runTest(testDispatcher) {
            settingsRepository = FakeSettingsRepository(initialLanguage = AppLanguage.ENGLISH)
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val state = vm.state.value
            assertEquals("en", state.selectedLanguageId)
            assertEquals("en", state.initialSelectedLanguageId)
            assertFalse(state.hasChange)
        }

    @Test
    fun `inisialisasi tetap stabil saat flow repository melempar error`() =
        runTest(testDispatcher) {
            settingsRepository.customSettingsFlow = flow {
                throw IOException("Disk IO error")
            }
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            // Nilai fallback default state tetap aman dan daftar bahasa tetap termuat
            assertEquals("id", vm.state.value.selectedLanguageId)
            assertEquals(AppLanguage.entries.size, vm.state.value.listLanguage.size)
        }

    // ── Interaksi Pemilihan Bahasa ───────────────────────────────────

    @Test
    fun `OnItemClick mengubah pilihan dan memperbarui flag hasChange`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            // Pilih bahasa lain ("en")
            vm.onEvent(LanguageSettingsEvent.OnItemClick("en"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("en", vm.state.value.selectedLanguageId)
            assertTrue(vm.state.value.hasChange)

            // Kembalikan pilihan ke bahasa semula ("id")
            vm.onEvent(LanguageSettingsEvent.OnItemClick("id"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("id", vm.state.value.selectedLanguageId)
            assertFalse(vm.state.value.hasChange)
        }

    @Test
    fun `OnBackClick memicu efek NavigateBack`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(LanguageSettingsEvent.OnBackClick)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals(listOf(LanguageSettingsEffect.NavigateBack), effects)
    }

    @Test
    fun `OnSaveClick sukses menyimpan ke repository menerapkan locale dan memicu efek Saved`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(LanguageSettingsEvent.OnItemClick("en"))
            vm.onEvent(LanguageSettingsEvent.OnSaveClick)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("en", settingsRepository.savedLanguage)
            assertEquals(listOf(LanguageSettingsEffect.Saved("en")), effects)
            assertEquals("en", AppLocale.locale.language)
        }

    @Test
    fun `OnSaveClick gagal memicu efek ShowSnackbar dan tidak mengirim Saved`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            settingsRepository.shouldThrowOnUpdate = true

            vm.onEvent(LanguageSettingsEvent.OnItemClick("ja"))
            vm.onEvent(LanguageSettingsEvent.OnSaveClick)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertTrue(effects.any { it is LanguageSettingsEffect.ShowSnackbar })
            assertFalse(effects.any { it is LanguageSettingsEffect.Saved })
        }

    // ── Pencarian Bahasa ─────────────────────────────────────────────

    @Test
    fun `OnSearchQueryChange memfilter daftar bahasa berdasarkan nama enum`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(LanguageSettingsEvent.OnSearchQueryChange("eng"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("eng", vm.state.value.searchQuery)
            assertEquals(listOf("en"), vm.state.value.listLanguage.map { it.id })
        }

    @Test
    fun `OnSearchQueryChange dengan query yang cocok beberapa bahasa`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            // "pan" cocok dengan sPANish dan jaPANese
            vm.onEvent(LanguageSettingsEvent.OnSearchQueryChange("pan"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals(listOf("es", "ja"), vm.state.value.listLanguage.map { it.id })
        }

    @Test
    fun `OnSearchQueryChange dengan query tidak cocok menghasilkan daftar kosong`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(LanguageSettingsEvent.OnSearchQueryChange("xyz_tidak_ada"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertTrue(vm.state.value.listLanguage.isEmpty())
        }

    @Test
    fun `OnSearchClear mengosongkan query dan mengembalikan seluruh daftar bahasa`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(LanguageSettingsEvent.OnSearchQueryChange("eng"))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })
            assertEquals(1, vm.state.value.listLanguage.size)

            vm.onEvent(LanguageSettingsEvent.OnSearchClear)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals("", vm.state.value.searchQuery)
            assertEquals(AppLanguage.entries.size, vm.state.value.listLanguage.size)
        }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun createViewModel() = LanguageSettingsViewModel(settingsRepository)

    private fun TestScope.collectEffects(
        vm: LanguageSettingsViewModel,
    ): MutableList<LanguageSettingsEffect> {
        val effects = mutableListOf<LanguageSettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }
}
