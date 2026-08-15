package com.example.nexcashapplication.feature.aset.domain.usecase

import com.example.nexcashapplication.feature.aset.domain.model.DeleteAccountDesision
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.aset.presentation.shared.resolveDeleteState
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ValidationDeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String): Flow<DeleteAccountDesision> {
        return combine(
            accountRepository.getAccountById(accountId),
            transactionRepository.getCountTransactionsByAccount(accountId),
        ) { account, transactionCount ->
            account.resolveDeleteState(transactionCount)
        }
    }
}