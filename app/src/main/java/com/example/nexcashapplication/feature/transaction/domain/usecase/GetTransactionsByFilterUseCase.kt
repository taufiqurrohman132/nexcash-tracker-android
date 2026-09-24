package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionFilter
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Sumber data tunggal halaman "Semua Transaksi".
 *
 * Menyeimbangkan [TransactionFilter] menjadi query repository + judul TopAppBar.
 * Satu tempat untuk semua cabang filter — diuji langsung dengan fake repository
 * tanpa SavedStateHandle/nav (ADR-002).
 */
class GetTransactionsByFilterUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) {

    /**
     * Judul TopAppBar. Nama kategori/akun di-fetch dari repository (bukan argumen route)
     * supaya ikut berubah saat entitas diedit di layar lain.
     */
    fun topBarTitle(filter: TransactionFilter): Flow<String> = when (filter) {
        TransactionFilter.None -> flowOf(TITLE_ALL)

        is TransactionFilter.ByCategory ->
            categoryRepository.getCategoryById(filter.categoryId)
                .map { category -> titleOr(category?.name) }

        is TransactionFilter.ByAccount ->
            accountRepository.getAccountById(filter.accountId)
                .map { account -> titleOr(account.name) }
    }

    /**
     * Daftar transaksi sesuai filter. Selalu memakai query ber-`deleted_at IS NULL`
     * (transaksi soft-delete tidak boleh muncul).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun transactions(filter: TransactionFilter): Flow<List<TransactionWithDetails>> =
        when (filter) {
            TransactionFilter.None -> transactionRepository.getAllTransactionsWithDetails()

            is TransactionFilter.ByAccount ->
                transactionRepository.getTransactionsByAccount(filter.accountId)

            is TransactionFilter.ByCategory ->
                // Subtree (induk + sub) dihitung di domain dari semua kategori,
                // lalu dipakai sebagai filter IN — menutup celah transaksi subkategori
                // tidak ikut terhitung di daftar induknya.
                categoryRepository.getAllCategory()
                    .map { all -> subtreeIds(all.filterNotNull(), filter.categoryId) }
                    .distinctUntilChanged()
                    .flatMapLatest { ids ->
                        transactionRepository.getTransactionsWithDetailsByCategoryIds(ids)
                    }
        }

    /**
     * Subtree rekursif induk → anak (BFS, tahan terhadap siklus data korup).
     * Mengembalikan id root beserta seluruh id subkategori turunannya.
     */
    private fun subtreeIds(all: List<Category>, rootId: String): List<String> {
        val childrenByParent = all.groupBy { it.parentId }
        val result = LinkedHashSet<String>()
        val queue = ArrayDeque(listOf(rootId))
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (!result.add(current)) continue
            childrenByParent[current]?.forEach { queue.add(it.id) }
        }
        return result.toList()
    }

    private fun titleOr(name: String?): String = name?.let { "$TITLE_PREFIX$it" } ?: TITLE_ALL

    private companion object {
        const val TITLE_ALL = "Semua Transaksi"
        const val TITLE_PREFIX = "Transaksi · "
    }
}
