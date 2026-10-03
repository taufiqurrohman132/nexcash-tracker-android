package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.domain.model.WeeklyTrend
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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

/**
 * Unit test alur cepat isi form [AddEditTransactionViewModel]:
 * tombol Next keyboard -> sheet Category -> pilih kategori -> lanjut sheet Account.
 *
 * Skenario yang dijaga:
 * - `OnCategoryClick(autoAdvance = true)` + kategori tanpa sub → sheet Account
 *   ikut terbuka begitu kategori selesai dipilih.
 * - kategori ber-sub → sheet Category **tetap** terbuka sampai sub dipilih,
 *   baru lanjut ke Account.
 * - tap biasa (`autoAdvance = false`) → Account **tidak** ikut kebuka.
 * - tutup sheet pakai scrim di tengah alur cepat → flag dibatalkan.
 *
 * Route dipakai lewat konstruktor `internal` dengan [Route.TransactionRoute.AddEdit]
 * langsung; konstruktor produksi `@Inject` tetap membaca `SavedStateHandle.toRoute()`
 * yang tidak bisa jalan di JVM unit test (Bundle Android di-mock).
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; coroutine ViewModel
 * berjalan di scheduler virtual yang sama (waktu nyata tidak dipakai).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTransactionViewModelTest {

    private lateinit var testDispatcher: TestDispatcher

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Alur cepat (Next dari field Nama) ──────────────────────────────

    @Test
    fun `alur cepat - kategori tanpa sub langsung lanjut ke sheet Account`() =
        runTest(testDispatcher) {
            val vm = createViewModel(listOf(group("c1", "Makanan")))

            vm.onEvent(AddEditTransactionEvent.OnCategoryClick(autoAdvance = true))
            advanceUntilIdle()
            assertTrue(vm.state.value.showCategoryBottomSheet)

            vm.onEvent(AddEditTransactionEvent.OnCategorySelected(vm.model("c1")))
            advanceUntilIdle()

            assertEquals("c1", vm.state.value.selectedCategoryId)
            assertFalse(vm.state.value.showCategoryBottomSheet)
            assertTrue(vm.state.value.showAccountBottomSheet)
        }

    @Test
    fun `alur cepat - kategori ber-sub menahan sheet sampai sub dipilih baru lanjut`() =
        runTest(testDispatcher) {
            val vm = createViewModel(listOf(group("p1", "Makanan", subIds = listOf("p1s1"))))

            vm.onEvent(AddEditTransactionEvent.OnCategoryClick(autoAdvance = true))
            advanceUntilIdle()

            // induk ber-sub: kategori terpilih tapi sheet belum boleh tutup
            vm.onEvent(AddEditTransactionEvent.OnCategorySelected(vm.model("p1")))
            advanceUntilIdle()
            assertEquals("p1", vm.state.value.selectedCategoryId)
            assertTrue(vm.state.value.showCategoryBottomSheet)
            assertFalse(vm.state.value.showAccountBottomSheet)

            // sub dipilih: barulah sheet tutup + lanjut ke Account
            vm.onEvent(AddEditTransactionEvent.OnCategorySelected(vm.model("p1s1")))
            advanceUntilIdle()
            assertEquals("p1s1", vm.state.value.selectedCategoryId)
            assertFalse(vm.state.value.showCategoryBottomSheet)
            assertTrue(vm.state.value.showAccountBottomSheet)
        }

    // ── Jalur biasa (tap baris Category) ───────────────────────────────

    @Test
    fun `tap biasa - memilih kategori tidak membuka sheet Account otomatis`() =
        runTest(testDispatcher) {
            val vm = createViewModel(listOf(group("c1", "Makanan")))

            vm.onEvent(AddEditTransactionEvent.OnCategoryClick())
            advanceUntilIdle()
            vm.onEvent(AddEditTransactionEvent.OnCategorySelected(vm.model("c1")))
            advanceUntilIdle()

            assertEquals("c1", vm.state.value.selectedCategoryId)
            assertFalse(vm.state.value.showCategoryBottomSheet)
            assertFalse(vm.state.value.showAccountBottomSheet)
        }

    @Test
    fun `tutup sheet dengan scrim di tengah alur cepat membatalkan lanjutan`() =
        runTest(testDispatcher) {
            val vm = createViewModel(listOf(group("c1", "Makanan")))

            vm.onEvent(AddEditTransactionEvent.OnCategoryClick(autoAdvance = true))
            advanceUntilIdle()

            // scrim menutup sheet (event tanpa autoAdvance) → flag dibuang
            vm.onEvent(AddEditTransactionEvent.OnCategoryClick())
            advanceUntilIdle()
            assertFalse(vm.state.value.showCategoryBottomSheet)

            // buka lagi lewat tap biasa, pilih kategori → Account tetap tertutup
            vm.onEvent(AddEditTransactionEvent.OnCategoryClick())
            advanceUntilIdle()
            vm.onEvent(AddEditTransactionEvent.OnCategorySelected(vm.model("c1")))
            advanceUntilIdle()

            assertFalse(vm.state.value.showAccountBottomSheet)
        }

    // ── Peralatan ──────────────────────────────────────────────────────

    private fun createViewModel(categories: List<CategoryAndSubcategory>) =
        AddEditTransactionViewModel(
            route = Route.TransactionRoute.AddEdit(),
            transactionRepository = FakeTransactionRepository(),
            categoryRepository = SeedCategoryRepository(categories),
            accountRepository = FakeAccountRepository(),
        )

    /** Ambil model kategori (induk maupun sub) persis seperti dibaca sheet. */
    private fun AddEditTransactionViewModel.model(id: String): TransactionCategoryItemModel =
        state.value.availableCategories
            .flatMap { listOf(it.categoriItem) + it.subCategoriesItem }
            .first { it.id == id }

    private fun group(
        id: String,
        name: String,
        subIds: List<String> = emptyList(),
    ) = CategoryAndSubcategory(
        category = Category(
            id = id,
            parentId = null,
            name = name,
            type = TypeCategory.EXPENSE,
            iconId = "food",
            color = "#17C3E8",
        ),
        subCategories = subIds.map { subId ->
            Category(
                id = subId,
                parentId = id,
                name = "sub $subId",
                type = TypeCategory.EXPENSE,
                iconId = "food",
                color = "#17C3E8",
            )
        },
    )
}

/**
 * [CategoryRepository] minimal untuk test ini — hanya
 * [getCategoriesAndSubCategories] yang dipakai ViewModel; metode lain tidak
 * pernah dipanggil jadi sengaja melempar supaya ketahuan bila tiba-tiba terpakai.
 */
private class SeedCategoryRepository(
    private val rows: List<CategoryAndSubcategory>,
) : CategoryRepository {

    override suspend fun getCategoriesAndSubCategories(): List<CategoryAndSubcategory> = rows

    override fun getCategoryById(id: String): Flow<Category?> = tidakDipakai()
    override fun getAllCategory(): Flow<List<Category?>> = tidakDipakai()
    override fun searchCategories(query: String, type: String): Flow<List<Category>> =
        tidakDipakai()

    override fun getExpenseCategories(): Flow<List<Category>> = tidakDipakai()
    override fun getIncomeCategories(): Flow<List<Category>> = tidakDipakai()
    override fun getSubCategories(parentId: String): Flow<List<Category>> = tidakDipakai()
    override suspend fun getParentCategories(): List<Category> = tidakDipakai()
    override suspend fun getParentCategoriesNameByType(type: String): List<CategoryName> =
        tidakDipakai()

    override fun getWeeklyTrend(
        typeTransaction: String?,
        categoryId: String?,
    ): Flow<WeeklyTrend> = tidakDipakai()

    override suspend fun insertCategory(category: Category): Unit = tidakDipakai()
    override suspend fun updateCategory(category: Category): Unit = tidakDipakai()
    override suspend fun deleteCategory(
        categoryId: String,
        resolution: CategoryDeleteResolution,
    ): Unit = tidakDipakai()

    override suspend fun countByName(name: String, type: String, parentId: String?): Int =
        tidakDipakai()

    override suspend fun countTransactionsByCategory(categoryId: String): Int = tidakDipakai()
    override suspend fun countSubCategories(categoryId: String): Int = tidakDipakai()

    private fun tidakDipakai(): Nothing =
        error("SeedCategoryRepository hanya dipakai untuk getCategoriesAndSubCategories")
}
