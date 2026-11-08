package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.presentation.currency.FakeSettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.toHex
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

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

    // ── Inisialisasi State ──────────────────────────────────────────

    @Test
    fun `inisialisasi memuat preferensi aktif dari repository`() = runTest(testDispatcher) {
        val testColorHex = "#FF5722"
        settingsRepository = FakeSettingsRepository(
            initialCurrency = "USD",
            initialLanguage = AppLanguage.ENGLISH,
            initialThemeMode = AppThemeMode.DARK,
            initialColor = testColorHex,
        )

        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        val state = vm.state.value
        assertEquals("USD", state.currency)
        assertEquals("en", state.language)
        assertEquals(AppThemeMode.DARK, state.themeMode)
        assertEquals(testColorHex, state.color.toHex())
    }

    @Test
    fun `inisialisasi dengan warna kosong menggunakan PrimaryCyan bawaan`() = runTest(testDispatcher) {
        settingsRepository = FakeSettingsRepository(initialColor = "")
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertEquals(PrimaryCyan, vm.state.value.color)
    }

    @Test
    fun `inisialisasi tetap stabil saat flow repository melempar error`() = runTest(testDispatcher) {
        settingsRepository.customSettingsFlow = flow {
            throw IOException("Gagal membaca DataStore")
        }
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        // State tetap memakai default tanpa crash
        assertEquals(PrimaryCyan, vm.state.value.color)
        assertEquals(AppThemeMode.SYSTEM, vm.state.value.themeMode)
    }

    // ── Menu Navigation ──────────────────────────────────────────────

    @Test
    fun `OnMenuItemClick TEMA_WARNA membuka bottom sheet tanpa navigasi`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.TEMA_WARNA))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertTrue(vm.state.value.showThemeColorSheet)
        assertTrue(effects.isEmpty())
    }

    @Test
    fun `OnMenuItemClick destinasi lain memicu efek NavigateTo`() = runTest(testDispatcher) {
        val destinations = listOf(
            SettingsDestinations.BAHASA,
            SettingsDestinations.MATA_UANG,
            SettingsDestinations.NOTIFIKASI,
            SettingsDestinations.KATEGORI,
            SettingsDestinations.PUSAT_BANTUAN,
        )

        for (dest in destinations) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(SettingsEvent.OnMenuItemClick(dest))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertFalse(vm.state.value.showThemeColorSheet)
            assertEquals(listOf(SettingsEffect.NavigateTo(dest)), effects)
        }
    }

    @Test
    fun `OnThemeSheetDismiss menutup bottom sheet`() = runTest(testDispatcher) {
        val vm = createViewModel()
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        vm.onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.TEMA_WARNA))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })
        assertTrue(vm.state.value.showThemeColorSheet)

        vm.onEvent(SettingsEvent.OnThemeSheetDismiss)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })
        assertFalse(vm.state.value.showThemeColorSheet)
    }

    // ── Perubahan Tema ───────────────────────────────────────────────

    @Test
    fun `OnThemeSelected sukses memperbarui tema di repository dan merefleksikan ke state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            vm.onEvent(SettingsEvent.OnThemeSelected(AppThemeMode.LIGHT))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals(AppThemeMode.LIGHT, settingsRepository.savedThemeMode)
            assertEquals(AppThemeMode.LIGHT, vm.state.value.themeMode)
        }

    @Test
    fun `OnThemeSelected gagal memicu efek ShowSnackbar`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        settingsRepository.shouldThrowOnUpdate = true

        vm.onEvent(SettingsEvent.OnThemeSelected(AppThemeMode.DARK))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertTrue(effects.any { it is SettingsEffect.ShowSnackbar })
    }

    // ── Perubahan Warna ──────────────────────────────────────────────

    @Test
    fun `OnColorSelected sukses memperbarui warna hex di repository dan merefleksikan ke state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val targetColor = Color(0xFF00E676)
            vm.onEvent(SettingsEvent.OnColorSelected(targetColor))
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val expectedHex = targetColor.toHex()
            assertEquals(expectedHex, settingsRepository.savedColor)
            assertEquals(expectedHex, vm.state.value.color.toHex())
        }

    @Test
    fun `OnColorSelected gagal memicu efek ShowSnackbar`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        settingsRepository.shouldThrowOnUpdate = true

        vm.onEvent(SettingsEvent.OnColorSelected(Color.Blue))
        awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

        assertTrue(effects.any { it is SettingsEffect.ShowSnackbar })
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun createViewModel() = SettingsViewModel(settingsRepository)

    private fun TestScope.collectEffects(
        vm: SettingsViewModel,
    ): MutableList<SettingsEffect> {
        val effects = mutableListOf<SettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }
}
