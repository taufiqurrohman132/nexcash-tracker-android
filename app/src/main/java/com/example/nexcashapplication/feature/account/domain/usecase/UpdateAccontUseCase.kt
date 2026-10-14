package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class UpdateAccontUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    /**
     * Perbarui akun yang sudah ada.
     *
     * [CancellationException] sengaja diteruskan, bukan diubah jadi `Result.failure`:
     * pembatalan (mis. ViewModel di-cleared) bukan kegagalan bisnis, dan menelannya
     * membuat pemanggil menampilkan "gagal simpan" di scope yang sudah mati.
     * Hanya kegagalan nyata yang menjadi `Result.failure`.
     */
    suspend operator fun invoke(
        account: AccountModel,
    ): Result<Unit> {
        return try {
            accountRepository.updateAccount(account)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}