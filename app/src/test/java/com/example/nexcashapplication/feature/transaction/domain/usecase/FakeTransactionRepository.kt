package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * In-memory [TransactionRepository] khusus unit test fitur transaksi — dipakai
 * test `GetTransactionsByFilterUseCase`, `AllTransactionsViewModel`,
 * `AddEditTransactionViewModel`, dan `DetailTransactionViewModel`.
 *
 * Metode yang dipakai test bersifat reaktif seperti Room (seed dulu sebelum
 * membuat ViewModel); sisanya sengaja melempar supaya gagal jelas bila tiba-tiba
 * terpakai.
 *
 * Pemicu error/hold disediakan lewat [failGetAllWithDetails], [failLoadById],
 * [failDelete], [failInsert], dan [deleteGate] untuk menguji jalur `.catch`,
 * guard hapus dobel, dan snackbar gagal.
 */
class FakeTransactionRepository : TransactionRepository {

    private val withDetails = MutableStateFlow<List<TransactionWithDetails>>(emptyList())

    /** Melempar saat [getAllTransactionsWithDetails] dikumpulkan (jalur error). */
    var failGetAllWithDetails = false

    /** Melempar saat [getTransactionWithDetailsById] dikumpulkan (jalur error load). */
    var failLoadById = false

    /** Melempar di [deleteTransaction] — hapus gagal. */
    var failDelete = false

    /** Melempar di [insertTransaction] — salin/simpan gagal. */
    var failInsert = false

    /**
     * Ditahan [deleteTransaction] sampai di-complete — menahan proses hapus supaya
     * test bisa mengetuk konfirmasi kedua di tengah jalan.
     */
    var deleteGate: CompletableDeferred<Unit>? = null

    /** Id yang benar-benar dihapus / transaksi yang benar-banar disisipkan. */
    val deletedIds = mutableListOf<String>()
    val insertedTransactions = mutableListOf<Transaction>()

    fun seed(vararg rows: TransactionWithDetails) {
        withDetails.value = withDetails.value + rows
    }

    override fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> =
        if (failGetAllWithDetails) flow { error("query rusak") } else withDetails

    override fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>> =
        withDetails.map { list -> list.filter { it.transaction.accountId == accountId } }

    override fun getTransactionsWithDetailsByCategoryIds(
        categoryIds: List<String>,
    ): Flow<List<TransactionWithDetails>> =
        withDetails.map { list -> list.filter { it.transaction.categoryId in categoryIds } }

    override fun getMonthlySummary(yearMonth: String): Flow<MonthlySummary> = unused()

    override fun getObserverTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> =
        unused()

    override fun getObserverTransactionsWithDetailsByMonth(
        yearMonth: String,
        categoryFilter: String?,
    ): Flow<List<TransactionWithDetails>> = unused()

    override fun getTotalExpenseByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Long> =
        unused()

    override fun getExpenseByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> = unused()

    override fun getIncomeByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> = unused()

    override fun getUsedAmountByCategory(
        categoryId: String,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<Long> = unused()

    override fun getUsedAmountsForCategories(
        categoryIds: List<String>,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<List<CategoryExpenseResult>> = unused()

    override fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>> =
        withDetails.map { list ->
            list.map { it.transaction }.filter { it.categoryId in categoryIds }
        }

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionWithDetails>> = unused()

    override fun getRecentTransactionsByCategory(
        limit: Int,
        categoryId: String,
    ): Flow<List<TransactionWithDetails>> = unused()

    override fun getAllTransactions(): Flow<List<Transaction>> = unused()

    override fun getTransactionById(id: String): Flow<Transaction> = unused()

    override fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails?> =
        if (failLoadById) flow { error("query rusak") }
        else withDetails.map { list -> list.firstOrNull { it.transaction.id == id } }

    override fun getCountTransactionsByAccount(accountId: String): Flow<Int> =
        withDetails.map { list -> list.count { it.transaction.accountId == accountId } }

    override suspend fun countTransactionsInRange(start: Long, end: Long): Int = unused()

    override suspend fun getTransactionByIdOnce(id: String): TransactionEntity = unused()

    override fun searchTransactions(query: String): Flow<List<TransactionWithDetails>> =
        unused()

    override suspend fun insertTransaction(transaction: Transaction) {
        if (failInsert) error("simpan gagal")
        insertedTransactions += transaction
        withDetails.value = withDetails.value + TransactionWithDetails(transaction = transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction): Unit = unused()

    override suspend fun deleteTransaction(transactionId: String) {
        deleteGate?.await()
        if (failDelete) error("hapus gagal")
        deletedIds += transactionId
        // meniru flow Room: setelah baris hilang, observer by-id mengirim null
        withDetails.value = withDetails.value.filterNot { it.transaction.id == transactionId }
    }

    private fun unused(): Nothing =
        throw UnsupportedOperationException("Tidak dipakai di unit test GetTransactionsByFilterUseCase")
}
