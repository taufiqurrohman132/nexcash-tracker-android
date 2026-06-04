package com.example.nexcashapplication.feature.category.data

import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.category.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getMonthlySummary(bulan: Int, tahun: Int): Flow<MonthlySummary> {
        val monthStr = String.format(Locale.US,"%02d", bulan)
        val yearStr = tahun.toString()
        return transactionDao.getMonthlySummary(monthStr, yearStr).map {
            it.toDomain(bulan, tahun)
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(10)
    }
}