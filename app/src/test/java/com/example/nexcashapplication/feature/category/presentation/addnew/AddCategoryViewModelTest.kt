package com.example.nexcashapplication.feature.category.presentation.addnew

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.datasource.local.datastore.PreferencesKeys
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetCategoryByIdUseCase
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.common.extentions.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddCategoryViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var preferencesRepository: FakePreferencesRepository
    private lateinit var categoryUseCases: CategoryUseCases

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        categoryRepository = FakeCategoryRepository()
        preferencesRepository = FakePreferencesRepository()
        categoryUseCases = CategoryUseCases(
            getCategoryById = GetCategoryByIdUseCase(categoryRepository),
            deleteCategory = DeleteCategoryUseCase(categoryRepository),
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        categoryId: String? = null,
        parentId: String? = null,
    ): AddCategoryViewModel {
        return AddCategoryViewModel(
            route = Route.CategoryRoute.AddNew(categoryId = categoryId, parentId = parentId),
            repository = categoryRepository,
            preferencesRepository = preferencesRepository,
            categoryUseCases = categoryUseCases,
        )
    }

    @Test
    fun `mode tambah baru - memuat recent colors dan default recent icons ketika preferences kosong`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals("", state.id)
            assertEquals("", state.name)
            // Preferences kosong -> fallback ke CategoryIcons.recentIcons
            assertEquals(CategoryIcons.recentIcons, state.recentIcons)
        }

    @Test
    fun `mode tambah baru - recent colors dan icons dimuat dari preferences jika tersedia`() =
        runTest(testDispatcher) {
            val savedColors = listOf("#FF0000", "#00FF00")
            val savedIcons = listOf("bike", "car")
            preferencesRepository.seedList(PreferencesKeys.RECENT_COLORS, savedColors)
            preferencesRepository.seedList(PreferencesKeys.RECENT_ICONS, savedIcons)

            val vm = createViewModel()
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals(savedColors.map { it.toColor() }, state.recentColors)
            assertEquals(savedIcons.map { CategoryIcons.fromId(it) }, state.recentIcons)
        }

    @Test
    fun `mode subkategori - memuat info kategori induk ke state parentCategory`() =
        runTest(testDispatcher) {
            val parent = Category(
                id = "p-1",
                name = "Makanan",
                type = TypeCategory.EXPENSE,
                color = "#FF9800",
                iconId = "food",
            )
            categoryRepository.seed(parent)

            val vm = createViewModel(parentId = "p-1")
            advanceUntilIdle()

            val parentUi = vm.state.value.parentCategory
            assertNotNull(parentUi)
            assertEquals("p-1", parentUi?.id)
            assertEquals("Makanan", parentUi?.name)
        }

    @Test
    fun `mode edit - memuat kategori yang ada dan mengisinya ke form state`() =
        runTest(testDispatcher) {
            val existing = Category(
                id = "cat-edit",
                name = "Bensin",
                type = TypeCategory.EXPENSE,
                color = "#00BCD4",
                iconId = "car",
                note = "Pengeluaran mingguan",
            )
            categoryRepository.seed(existing)

            val vm = createViewModel(categoryId = "cat-edit")
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals("cat-edit", state.id)
            assertEquals("Bensin", state.name)
            assertEquals(TypeCategory.EXPENSE, state.typeCategory)
            assertEquals("Pengeluaran mingguan", state.note)
            assertEquals("car", state.selectedIcon?.id)
            assertEquals("#00BCD4".toColor(), state.selectedColor)
        }

    @Test
    fun `event OnNameChanged - memperbarui properti name pada state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(AddCategoryEvent.OnNameChanged("Belanja Bulanan"))

            assertEquals("Belanja Bulanan", vm.state.value.name)
            assertTrue(vm.state.value.isValid)
        }

    @Test
    fun `event OnNotesChanged - memperbarui properti note pada state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(AddCategoryEvent.OnNotesChanged("Catatan baru"))

            assertEquals("Catatan baru", vm.state.value.note)
        }

    @Test
    fun `event OnTypeSelected - memperbarui typeCategory pada state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(AddCategoryEvent.OnTypeSelected(TypeCategory.INCOME))

            assertEquals(TypeCategory.INCOME, vm.state.value.typeCategory)
        }

    @Test
    fun `event OnChangeIcon - memperbarui selectedIcon dan mengurutkan recentIcons serta menyimpannya ke preference`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val newIcon = CategoryIcons.fromId("flight")
            vm.onEvent(AddCategoryEvent.OnChangeIcon(newIcon))
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals(newIcon, state.selectedIcon)
            assertEquals(newIcon, state.recentIcons.first())

            val savedIcons = preferencesRepository.getList(
                PreferencesKeys.RECENT_ICONS,
                ListSerializer(String.serializer()),
            ).first()
            assertEquals("flight", savedIcons.first())
        }

    @Test
    fun `event OnColorSelected - memperbarui selectedColor, recentColors, menutup dialog, dan menyimpan ke preference`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val newColor = Color(0xFFE91E63)
            vm.onEvent(AddCategoryEvent.OnColorSelected(newColor))
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals(newColor, state.selectedColor)
            assertEquals(newColor, state.recentColors.first())
            assertFalse(state.showColorPalletDialog)

            val savedColors = preferencesRepository.getList(
                PreferencesKeys.RECENT_COLORS,
                ListSerializer(String.serializer()),
            ).first()
            assertEquals(newColor.toHex(), savedColors.first())
        }

    @Test
    fun `dialog color picker - OnAddColorClicked membuka dialog dan OnColorPickerDismiss menutupnya`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(AddCategoryEvent.OnAddColorClicked)
            assertTrue(vm.state.value.showColorPalletDialog)

            vm.onEvent(AddCategoryEvent.OnColorPickerDismiss)
            assertFalse(vm.state.value.showColorPalletDialog)
        }

    @Test
    fun `event OnBackClicked - mengirim efek NavigateBack`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val effects = mutableListOf<CategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(AddCategoryEvent.OnBackClicked)
            advanceUntilIdle()

            assertEquals(listOf(CategoryEffect.NavigateBack), effects)
        }

    @Test
    fun `event OnSaveClicked ketika valid mode tambah - insertCategory ke repository dan mengirim NavigateBack`() =
        runTest(testDispatcher) {
            val vm = createViewModel(parentId = "parent-x")
            advanceUntilIdle()

            val effects = mutableListOf<CategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(AddCategoryEvent.OnNameChanged("Listrik"))
            vm.onEvent(AddCategoryEvent.OnNotesChanged("Token bulanan"))
            vm.onEvent(AddCategoryEvent.OnSaveClicked)
            advanceUntilIdle()

            assertEquals(listOf(CategoryEffect.NavigateBack), effects)
            assertEquals(1, categoryRepository.insertedCategories.size)
            val inserted = categoryRepository.insertedCategories.first()
            assertEquals("Listrik", inserted.name)
            assertEquals("parent-x", inserted.parentId)
            assertEquals("Token bulanan", inserted.note)
        }

    @Test
    fun `event OnSaveClicked ketika valid mode edit - updateCategory ke repository dan mengirim NavigateBack`() =
        runTest(testDispatcher) {
            val existing = Category(
                id = "cat-update",
                name = "Internet Lama",
                type = TypeCategory.EXPENSE,
                color = "#123456",
                iconId = "wifi",
            )
            categoryRepository.seed(existing)

            val vm = createViewModel(categoryId = "cat-update")
            advanceUntilIdle()

            val effects = mutableListOf<CategoryEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(AddCategoryEvent.OnNameChanged("Internet Baru"))
            vm.onEvent(AddCategoryEvent.OnSaveClicked)
            advanceUntilIdle()

            assertEquals(listOf(CategoryEffect.NavigateBack), effects)
            assertEquals(1, categoryRepository.updatedCategories.size)
            val updated = categoryRepository.updatedCategories.first()
            assertEquals("cat-update", updated.id)
            assertEquals("Internet Baru", updated.name)
        }

    @Test
    fun `event OnSaveClicked ketika tidak valid (name kosong) - tidak menyimpan ke repository`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(AddCategoryEvent.OnNameChanged("   "))
            vm.onEvent(AddCategoryEvent.OnSaveClicked)
            advanceUntilIdle()

            assertTrue(categoryRepository.insertedCategories.isEmpty())
            assertTrue(categoryRepository.updatedCategories.isEmpty())
        }
}
