package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.presentation.main.AlokasiMode
import com.example.nexcashapplication.feature.statistics.presentation.main.RealisasiTipe
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Sumber data donut "Alokasi Dana".
 *
 * - [AlokasiMode.ALOKASI] → budget per kategori yang di-set user (budget hanya
 *   ada di kategori pengeluaran).
 * - [AlokasiMode.REALISASI] → aktual per kategori, dipilih lewat [realisasiTipe]:
 *   pengeluaran ([RealisasiTipe.PENGELUARAN]) atau pemasukan
 *   ([RealisasiTipe.PENDAPATAN]).
 *
 * Nominal subkategori digulung ke induk akarnya ([rollupToRoot]) supaya
 * sejajar dengan budget yang diset di kategori induk — slice donut selalu
 * kategori induk.
 */
class GetCategoryAllocationUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    /**
     * [budgetsFlow] boleh dikirim dari luar supaya query `getBudgetsByMonth`
     * tidak dijalankan dua kali (use case ini + [GetBudgetHealthUseCase] pada
     * combine yang sama di StatistikViewModel).
     */
    operator fun invoke(
        monthStart: Long,
        monthEnd: Long,
        alocationMode: AlokasiMode,
        realisasiTipe: RealisasiTipe = RealisasiTipe.PENGELUARAN,
        budgetsFlow: Flow<List<Budget>> = budgetRepository.getBudgetsByMonth(monthStart, monthEnd),
    ): Flow<List<AlokasiKategoriUi>> {
        val amountsFlow = when (alocationMode) {
            AlokasiMode.ALOKASI -> budgetsFlow.map { budgets ->
                budgets.associate { budget -> budget.categoryId to budget.amount }
            }

            AlokasiMode.REALISASI -> when (realisasiTipe) {
                RealisasiTipe.PENGELUARAN ->
                    transactionRepository.getExpenseByCategoryByMonth(monthStart, monthEnd)

                RealisasiTipe.PENDAPATAN ->
                    transactionRepository.getIncomeByCategoryByMonth(monthStart, monthEnd)
            }
        }

        return combine(
            amountsFlow, categoryRepository.getAllCategory(),
        ) { amounts, categories ->
            val alive = categories.filterNotNull()
            val rolled = rollupToRoot(amounts, alive)

            // total dulu dari nominal yang benar-benar tampil — nominal milik
            // kategori yang sudah tidak ada di tabel tidak ikut memperbesar
            // penyebut persentase
            val items = rolled.mapNotNull { (categoryId, amount) ->
                // nominal 0 tidak ikut — dulu semua kategori tampil sehingga
                // donut & legend penuh irisan 0%
                if (amount <= 0L) return@mapNotNull null
                val category = alive.find { it.id == categoryId }
                        ?: return@mapNotNull null

                AlokasiKategoriUi(
                    kategoriId = category.id,
                    nama = category.name,
                    persentase = 0f, // diisi setelah total diketahui
                    nominal = amount,
                    colorHex = category.color,
                )
            }

            val totalAmount = items.sumOf { it.nominal }
            if (totalAmount <= 0L) {
                emptyList()
            } else {
                items
                        .map {
                            // Float division — versi Long `amount / totalAmount`
                            // membuang pecahan dan hampir selalu 0
                            it.copy(persentase = it.nominal * 100f / totalAmount)
                        }
                        .sortedByDescending { it.nominal }
            }
        }
                // rollup + O(n²) `find` + sort: jangan jalan di main thread
                // (StatistikViewModel mengumpulkan ini pas transisi pindah tab)
                .flowOn(Dispatchers.Default)
    }

    /**
     * Jumlahkan nominal per kategori ke induk akarnya (induk dari induk, dst).
     * Induk yang hilang dari [categories] tidak diikuti — nominal tetap
     * menempel di kategori asal supaya tidak hilang dari donut.
     */
    private fun rollupToRoot(
        amounts: Map<String, Long>,
        categories: List<Category>,
    ): Map<String, Long> {
        if (amounts.isEmpty()) return amounts

        val parentOf = categories.associate { it.id to it.parentId }

        fun rootOf(id: String): String {
            var current = id
            val guard = mutableSetOf<String>()
            while (true) {
                val parent = parentOf[current] ?: return current
                // anti-loop untuk data induk yang saling menunjuk
                if (parent == current || parent !in parentOf || !guard.add(current)) return current
                current = parent
            }
        }

        val rolled = mutableMapOf<String, Long>()
        amounts.forEach { (categoryId, amount) ->
            val root = rootOf(categoryId)
            rolled[root] = (rolled[root] ?: 0L) + amount
        }
        return rolled
    }
}
