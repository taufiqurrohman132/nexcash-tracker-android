package com.example.nexcashapplication.feature.notification.presentation.edit

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.notification.FakeNotificationPreferenceRepository
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.settings.presentation.currency.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var notificationPreferenceRepository: FakeNotificationPreferenceRepository
    private lateinit var settingsRepository: FakeSettingsRepository

    private val today = LocalDate.now()
    private lateinit var monthStart: LocalDate
    private lateinit var monthEnd: LocalDate

    private val category = Category(
        id = "cat-food",
        parentId = null,
        name = "Makanan",
        type = TypeCategory.EXPENSE,
        iconId = "food",
        color = "#FF5722",
    )

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        val period = getMonthlyPeriod(today)
        monthStart = period.first
        monthEnd = period.second

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        notificationPreferenceRepository = FakeNotificationPreferenceRepository()
        settingsRepository = FakeSettingsRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `inisialisasi - memuat master switch dan daftar budget aktif dengan preferensinya`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )
            notificationPreferenceRepository.seed(
                BudgetAlertPreference(budgetId = "b1", enabled = true, thresholds = setOf(80, 100)),
            )

            val vm = createViewModel()
            advanceUntilIdle()

            val state = vm.state.value
            assertTrue(state.masterEnabled)
            assertFalse(state.isLoading)
            assertEquals(1, state.budgets.size)
            val budgetUi = state.budgets.first()
            assertEquals("b1", budgetUi.budgetId)
            assertEquals("Makanan", budgetUi.name)
            assertTrue(budgetUi.enabled)
            assertEquals(setOf(80, 100), budgetUi.thresholds)
        }

    @Test
    fun `MasterToggled - mengubah setting dan meminta izin jika izin belum diberikan`() =
        runTest(testDispatcher) {
            settingsRepository.updateNotificationEnabled(false)

            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            assertFalse(vm.state.value.masterEnabled)

            // Izin dicabut / belum ada
            vm.onEvent(NotificationSettingsEvent.PermissionChecked(false))
            assertFalse(vm.state.value.permissionGranted)

            vm.onEvent(NotificationSettingsEvent.MasterToggled(true))
            advanceUntilIdle()

            assertTrue(vm.state.value.masterEnabled)
            assertTrue(settingsRepository.settings.first().notificationMasterEnabled)
            assertEquals(
                listOf(NotificationSettingsEffect.RequestNotificationPermission),
                effects,
            )
        }

    @Test
    fun `BudgetToggled - memperbarui status enable pada repository`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )

            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(NotificationSettingsEvent.BudgetToggled(budgetId = "b1", enabled = false))
            advanceUntilIdle()

            val pref = notificationPreferenceRepository.observeForBudget("b1").first()
            assertNotNull(pref)
            assertFalse(pref!!.enabled)
        }

    @Test
    fun `ThresholdToggled - menambah atau menghapus threshold pada preferensi`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )
            // Awal: {80, 100}
            val vm = createViewModel()
            advanceUntilIdle()

            // Uncheck 80% -> sisa {100}
            vm.onEvent(NotificationSettingsEvent.ThresholdToggled(budgetId = "b1", percent = 80, selected = false))
            advanceUntilIdle()

            val pref = notificationPreferenceRepository.observeForBudget("b1").first()
            assertEquals(setOf(100), pref?.thresholds)
        }

    @Test
    fun `PermissionChecked dan PermissionRequestResult - memperbarui status permissionGranted di state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            assertTrue(vm.state.value.permissionGranted)

            vm.onEvent(NotificationSettingsEvent.PermissionChecked(false))
            assertFalse(vm.state.value.permissionGranted)

            vm.onEvent(NotificationSettingsEvent.PermissionRequestResult(true))
            assertTrue(vm.state.value.permissionGranted)
        }

    @Test
    fun `BackClicked - mengirim effect NavigateBack`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(NotificationSettingsEvent.BackClicked)
            advanceUntilIdle()

            assertEquals(listOf(NotificationSettingsEffect.NavigateBack), effects)
        }

    @Test
    fun `error - kegagalan saat membaca budget mengisi error dan mematikan loading`() =
        runTest(testDispatcher) {
            categoryRepository.throwOnLoad = RuntimeException("Gagal load budget")

            val vm = createViewModel()
            advanceUntilIdle()

            assertFalse(vm.state.value.isLoading)
            assertNotNull(vm.state.value.error)
            val error = vm.state.value.error
            assertTrue(
                error is UiText.Raw && error.value.contains("Gagal load budget"),
                "Pesan error terbawa: $error",
            )
        }

    private fun createViewModel() = NotificationSettingsViewModel(
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        notificationPreferenceRepository = notificationPreferenceRepository,
        settingsRepository = settingsRepository,
    )

    private fun TestScope.collectEffects(vm: NotificationSettingsViewModel): MutableList<NotificationSettingsEffect> {
        val effects = mutableListOf<NotificationSettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }
}
