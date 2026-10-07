package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionFilter
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit test [GetTransactionsByFilterUseCase] — logika per-filter halaman
 * "Semua Transaksi" (ADR-002): grouping by query, subtree subkategori, dan judul.
 */
class GetTransactionsByFilterUseCaseTest {

    private lateinit var transactionRepo: FakeTransactionRepository
    private lateinit var categoryRepo: FakeCategoryRepository
    private lateinit var accountRepo: FakeAccountRepository
    private lateinit var useCase: GetTransactionsByFilterUseCase

    // Pohon kategori: Makanan ─┬─ Makan Siang
    //                          └─ Masak
    //          Transportasi (terpisah)
    private val food = category("food", name = "Makanan", parentId = null)
    private val foodLunch = category("food-lunch", name = "Makan Siang", parentId = "food")
    private val foodCook = category("food-cook", name = "Masak", parentId = "food-lunch") // 3 level
    private val transport = category("transport", name = "Transportasi", parentId = null)

    @BeforeEach
    fun setUp() {
        transactionRepo = FakeTransactionRepository()
        categoryRepo = FakeCategoryRepository()
        accountRepo = FakeAccountRepository()
        useCase = GetTransactionsByFilterUseCase(transactionRepo, categoryRepo, accountRepo)
    }

    @Test
    fun `filter None mengembalikan semua transaksi yang di-seed`() = runTest {
        transactionRepo.seed(
            transaction("1", categoryId = "food"),
            transaction("2", categoryId = "transport"),
            transaction("3", categoryId = null),
        )

        val result = useCase.transactions(TransactionFilter.None).first()

        assertEquals(3, result.size)
    }

    @Test
    fun `ByCategory ikut subkategori turunan berlapis sampai level terdalam`() = runTest {
        categoryRepo.seed(food, foodLunch, foodCook, transport)
        transactionRepo.seed(
            transaction("induk", categoryId = "food"),
            transaction("anak", categoryId = "food-lunch"),
            transaction("cucu", categoryId = "food-cook"),
            transaction("lain", categoryId = "transport"),
        )

        val result = useCase.transactions(TransactionFilter.ByCategory("food")).first()

        assertEquals(
            setOf("induk", "anak", "cucu"),
            result.map { it.transaction.id }.toSet(),
            "Transaksi subkategori (termasuk turunan level ke-3) harus ikut, kategori lain tidak",
        )
    }

    @Test
    fun `ByCategory menambah id baru saat subkategori ditambahkan`() = runTest {
        categoryRepo.seed(food, transport)
        transactionRepo.seed(
            transaction("induk", categoryId = "food"),
            transaction("baru", categoryId = "food-new"),
        )

        // sebelum subkategori "food-new" dikenal → hanya induk
        assertEquals(
            listOf("induk"),
            useCase.transactions(TransactionFilter.ByCategory("food")).first()
                .map { it.transaction.id },
        )

        // setelah kategori baru muncul di repo → subtree ikut terhitung (reaktif)
        categoryRepo.seed(category("food-new", name = "Baru", parentId = "food"))
        assertEquals(
            setOf("induk", "baru"),
            useCase.transactions(TransactionFilter.ByCategory("food")).first()
                .map { it.transaction.id }.toSet(),
        )
    }

    @Test
    fun `ByAccount hanya mengembalikan transaksi akun tersebut`() = runTest {
        transactionRepo.seed(
            transaction("bca-1", categoryId = "food", accountId = "bca"),
            transaction("cash-1", categoryId = "food", accountId = "cash"),
            transaction("bca-2", categoryId = null, accountId = "bca"),
        )

        val result = useCase.transactions(TransactionFilter.ByAccount("bca")).first()

        assertEquals(
            listOf("bca-1", "bca-2"),
            result.map { it.transaction.id },
        )
    }

    @Test
    fun `judul None adalah Semua Transaksi`() = runTest {
        val title = useCase.topBarTitle(TransactionFilter.None).first()

        assertEquals(uiText(R.string.tx_title_all), title)
    }

    @Test
    fun `judul ByCategory memakai nama kategori dari repository`() = runTest {
        categoryRepo.seed(food)

        val title = useCase.topBarTitle(TransactionFilter.ByCategory("food")).first()

        assertEquals(uiText(R.string.tx_title_filter, "Makanan"), title)
    }

    @Test
    fun `judul ByAccount memakai nama akun dari repository`() = runTest {
        accountRepo.seed(sampleBankAccount(name = "BCA"))

        val title = useCase.topBarTitle(TransactionFilter.ByAccount("bca")).first()

        assertEquals(uiText(R.string.tx_title_filter, "BCA"), title)
    }

    // ── fixtures ────────────────────────────────────────────────────────────

    private fun category(id: String, name: String, parentId: String?) = Category(
        id = id,
        parentId = parentId,
        name = name,
        type = TypeCategory.EXPENSE,
        iconId = "pet",
        color = "#FF0000",
    )

    private fun transaction(
        id: String,
        categoryId: String?,
        accountId: String? = "bca",
    ) = TransactionWithDetails(
        transaction = Transaction(
            id = id,
            title = "Transaksi $id",
            amount = 10_000,
            categoryId = categoryId,
            description = null,
            typeTransaction = TransactionType.EXPENSE,
            accountId = accountId,
        ),
    )
}
