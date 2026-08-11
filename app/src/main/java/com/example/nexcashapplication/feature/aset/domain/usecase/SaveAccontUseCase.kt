package com.example.nexcashapplication.feature.aset.domain.usecase

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import javax.inject.Inject

class SaveAccontUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        account: AccountModel,
    ): Result<Unit> {
        return try {
            accountRepository.insertAccount(account)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}