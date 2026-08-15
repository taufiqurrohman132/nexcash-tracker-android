package com.example.nexcashapplication.feature.aset.domain.usecase

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountDetailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String): Flow<AccountModel> {
        val asset = accountRepository.getAccountById(accountId)
//            ?: throw NoSuchElementException("Asset not found")

//        val recentTransactions = transactionRepository.getRecentTransactions(
//
//        )

        return asset
    }
}