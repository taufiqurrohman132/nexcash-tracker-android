package com.example.nexcashapplication.feature.category.presentation.delete

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetCategoryByIdUseCase
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteCategoryViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var categoryUseCases: CategoryUseCases

    private val targetCategoryId = "cat-to-delete"

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        categoryRepository = FakeCategoryRepository()
        categoryUseCases = CategoryUseCases(
            getCategoryById = GetCategoryByIdUseCase(categoryRepository),
            deleteCategory = DeleteCategoryUseCase(categoryRepository),
        )

        categoryRepository.seed(
            Category(id = targetCategoryId, name = "Makanan", type = TypeCategory.EXPENSE, color = "#FF0000", iconId = "food"),
            Category(id = "other-cat-1", name = "Transportasi", type = TypeCategory.EXPENSE, color = "#00FF00", iconId = "car"),
            Category(id = "other-cat-2", name = "Belanja", type = TypeCategory.EXPENSE, color = "#0000FF", iconId = "cart"),
        )
        categoryRepository.transactionCountToReturn = 5
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DeleteCategoryViewModel {
        return DeleteCategoryViewModel(
            route = Route.CategoryRoute.Delete(
                categoryId = targetCategoryId,
                typeCategory = TypeCategory.EXPENSE,
            ),
            categoryRepository = categoryRepository,
            categoryUseCases = categoryUseCases,
        )
    }

    @Test
    fun `loadData sukses - memuat detail kategori, jumlah transaksi, dan kategori tujuan tanpa kategori yang dihapus`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val state = vm.state.value
            assertFalse(state.isLoading)
            assertEquals(targetCategoryId, state.categoryId)
            assertEquals("Makanan", state.categoryName)
            assertEquals(5, state.transactionCount)
            assertEquals(2, state.availableCategories.size)
            assertTrue(state.availableCategories.none { it.id == targetCategoryId })
        }

    @Test
    fun `loadData gagal - mengirim efek ShowError dan isLoading false`() =
        runTest(testDispatcher) {
            categoryRepository.throwOnLoad = RuntimeException("Database error")

            val vm = createViewModel()
            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }
            advanceUntilIdle()

            assertFalse(vm.state.value.isLoading)
            assertEquals(1, effects.size)
            assertTrue(effects.first() is DeleteCategoryEffect.ShowError)
        }

    @Test
    fun `event Cancel - mengirim efek NavigateBack`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(DeleteCategoryEvent.Cancel)
            advanceUntilIdle()

            assertEquals(listOf(DeleteCategoryEffect.NavigateBack), effects)
        }

    @Test
    fun `event ToggleDropdown - mengubah status isDropdownExpanded`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            assertFalse(vm.state.value.isDropdownExpanded)

            vm.onEvent(DeleteCategoryEvent.ToggleDropdown)
            assertTrue(vm.state.value.isDropdownExpanded)

            vm.onEvent(DeleteCategoryEvent.ToggleDropdown)
            assertFalse(vm.state.value.isDropdownExpanded)
        }

    @Test
    fun `event SelectOption - mengubah selectedOption dan menutup dropdown`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(DeleteCategoryEvent.ToggleDropdown)
            assertTrue(vm.state.value.isDropdownExpanded)

            vm.onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.DELETE_ALL_TRANSACTIONS))

            assertEquals(DeleteOption.DELETE_ALL_TRANSACTIONS, vm.state.value.selectedOption)
            assertFalse(vm.state.value.isDropdownExpanded)
        }

    @Test
    fun `event SelectTargetCategory - mengubah targetCategory dan menutup dropdown`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val target = CategoryDeleteItemUI(
                id = "other-cat-1",
                name = "Transportasi",
                icon = CategoryIcons.fromId("car"),
                iconColor = Color.Green,
            )

            vm.onEvent(DeleteCategoryEvent.ToggleDropdown)
            vm.onEvent(DeleteCategoryEvent.SelectTargetCategory(target))

            assertEquals(target, vm.state.value.targetCategory)
            assertFalse(vm.state.value.isDropdownExpanded)
            assertTrue(vm.state.value.isConfirmEnabled)
        }

    @Test
    fun `ConfirmDelete dengan MOVE_TO_CATEGORY - memanggil repository dengan MoveToCategory dan mengirim DeleteSuccess`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val target = CategoryDeleteItemUI(
                id = "other-cat-1",
                name = "Transportasi",
                icon = CategoryIcons.fromId("car"),
                iconColor = Color.Green,
            )
            vm.onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.MOVE_TO_CATEGORY))
            vm.onEvent(DeleteCategoryEvent.SelectTargetCategory(target))

            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(DeleteCategoryEvent.ConfirmDelete)
            advanceUntilIdle()

            assertEquals(listOf(DeleteCategoryEffect.DeleteSuccess), effects)
            assertEquals(targetCategoryId, categoryRepository.lastDeletedCategoryId)
            assertEquals(CategoryDeleteResolution.MoveToCategory("other-cat-1"), categoryRepository.lastDeleteResolution)
        }

    @Test
    fun `ConfirmDelete dengan DELETE_ALL_TRANSACTIONS - memanggil repository dengan DeleteAllTransactions dan mengirim DeleteSuccess`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.DELETE_ALL_TRANSACTIONS))

            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(DeleteCategoryEvent.ConfirmDelete)
            advanceUntilIdle()

            assertEquals(listOf(DeleteCategoryEffect.DeleteSuccess), effects)
            assertEquals(targetCategoryId, categoryRepository.lastDeletedCategoryId)
            assertEquals(CategoryDeleteResolution.DeleteAllTransactions, categoryRepository.lastDeleteResolution)
        }

    @Test
    fun `ConfirmDelete dengan UNCATEGORIZED - memanggil repository dengan Uncategorize dan mengirim DeleteSuccess`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.UNCATEGORIZED))

            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(DeleteCategoryEvent.ConfirmDelete)
            advanceUntilIdle()

            assertEquals(listOf(DeleteCategoryEffect.DeleteSuccess), effects)
            assertEquals(targetCategoryId, categoryRepository.lastDeletedCategoryId)
            assertEquals(CategoryDeleteResolution.Uncategorize, categoryRepository.lastDeleteResolution)
        }

    @Test
    fun `ConfirmDelete ketika tidak isConfirmEnabled - tidak memanggil deleteCategory ke repository`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            // Default option adalah MOVE_TO_CATEGORY dengan targetCategory = null (isConfirmEnabled = false)
            assertFalse(vm.state.value.isConfirmEnabled)

            vm.onEvent(DeleteCategoryEvent.ConfirmDelete)
            advanceUntilIdle()

            assertNull(categoryRepository.lastDeletedCategoryId)
            assertNull(categoryRepository.lastDeleteResolution)
        }

    @Test
    fun `ConfirmDelete gagal di repository - mengirim efek ShowError dan isSubmitting kembali false`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.UNCATEGORIZED))
            categoryRepository.throwOnDelete = RuntimeException("Gagal menghapus")

            val effects = mutableListOf<DeleteCategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(DeleteCategoryEvent.ConfirmDelete)
            advanceUntilIdle()

            assertFalse(vm.state.value.isSubmitting)
            assertEquals(1, effects.size)
            assertTrue(effects.first() is DeleteCategoryEffect.ShowError)
        }
}
