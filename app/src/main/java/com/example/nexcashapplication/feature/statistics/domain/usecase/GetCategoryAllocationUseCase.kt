package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.presentation.main.AlokasiMode
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryAllocationUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(
        monthStart: Long, monthEnd: Long, alocationMode: AlokasiMode,
    ): Flow<List<AlokasiKategoriUi>> {
        val amountsFlow = when (alocationMode) {
            AlokasiMode.ALOKASI -> budgetRepository.getBudgetsByMonth(
                monthStart, monthEnd,
            ).map { it.associate { budget -> budget.categoryId to budget.amount } }

            AlokasiMode.REALISASI -> transactionRepository.getExpenseByCategoryByMonth(monthStart, monthEnd)
        }

        return combine(
            amountsFlow, categoryRepository.getAllCategory(),
        ) { amounts, categories ->
            val totalAmount = amounts.values.sum()

            categories
                    .filterNotNull() // drop kategori null DI SINI, sebelum masuk transform
                    .mapNotNull { category ->
                        val amount = amounts[category.id] ?: 0L
                        // nominal 0 tidak ikut — dulu semua kategori tampil sehingga
                        // donut & legend penuh irisan 0%
                        if (amount <= 0L) return@mapNotNull null

                        AlokasiKategoriUi(
                            kategoriId = category.id,
                            nama = category.name,
                            // Float division — versi Long `amount / totalAmount`
                            // membuang pecahan dan hampir selalu 0
                            persentase = amount * 100f / totalAmount,
                            nominal = amount,
                            colorHex = category.color,
                        )
                    }
                    .sortedByDescending { it.nominal }
        }
    }
}