package com.example.nexcashapplication.feature.transaction.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.detail.mapper.toDeleteDialogUiState
import com.example.nexcashapplication.feature.transaction.presentation.detail.mapper.toDetailTransactionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel internal constructor(
    private val route: Route.TransactionRoute.Detail,
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<DetailTransactionState, DetailTransactionEvent, DetailTransactionEffect>(
    DetailTransactionState(),
) {

    /**
     * Konstruktor produksi (Hilt + Navigation): membaca typed route dari
     * [SavedStateHandle] lewat `toRoute()`.
     *
     * `toRoute()` membaca argumen lewat Bundle Android; di JVM unit test Bundle
     * di-mock, jadi test memakai konstruktor `internal` dengan
     * [Route.TransactionRoute.Detail] langsung — jalur di device tetap identik.
     * Pola yang sama dipakai `AddEditTransactionViewModel` dan `DetailCategoryViewModel`.
     */
    @Inject constructor(
        savedStateHandle: SavedStateHandle,
        transactionRepository: TransactionRepository,
    ) : this(
        route = savedStateHandle.toRoute<Route.TransactionRoute.Detail>(),
        transactionRepository = transactionRepository,
    )

    private val transactionId = route.transactionId

    init {
        loadTransaction(transactionId)
    }

    override fun onEvent(event: DetailTransactionEvent) {
        when (event) {
            DetailTransactionEvent.OnAttachmentClick -> {
                // TODO: buka viewer lampiran setelah mapper memetakan data attachment
                // (sekarang selalu null). Sengaja bukan TODO() — baris ini bisa diklik.
                viewModelScope.launch {
                    sendEffect(
                        DetailTransactionEffect.ShowSnackbar("Pratinjau lampiran belum tersedia"),
                    )
                }
            }

            DetailTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(DetailTransactionEffect.NavigateBack)
                }
            }

            DetailTransactionEvent.OnDeleteClick -> {
                updateState {
                    it.copy(
                        deleteDialogState = it.transaction?.toDeleteDialogUiState(),
                    )
                }
            }

            is DetailTransactionEvent.OnDeleteConfirm -> {
                handleDeleteTransaction(transactionId)
            }

            DetailTransactionEvent.OnDismissDeleteDialog -> {
                updateState {
                    it.copy(
                        deleteDialogState = null,
                    )
                }
            }

            DetailTransactionEvent.OnDuplicateClick -> {
                handleDuplicate()
            }

            DetailTransactionEvent.OnEditClick -> {
                viewModelScope.launch {
                    sendEffect(DetailTransactionEffect.NavigateToEdit(transactionId))
                }
            }

            DetailTransactionEvent.OnShareClick -> {
                handleShare()
            }
        }
    }

    fun loadTransaction(id: String) {
        transactionRepository.getTransactionWithDetailsById(id)
                .onEach { transactionWithDetails ->
                    if (transactionWithDetails == null) {
                        if (state.value.isDeleting) {
                            // null ini akibat delete kita sendiri; popBackStack sudah
                            // dikirim handleDeleteTransaction. Pop lagi = dua layar kebelakang.
                            return@onEach
                        }
                        // Transaksi udah kehapus — kemungkinan besar karena delete sukses,
                        // atau race condition lain. Navigate back, bukan crash atau nampilin state kosong.
                        sendEffect(DetailTransactionEffect.NavigateBack)
                        return@onEach
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            transaction = transactionWithDetails.toDetailTransactionUiModel(),
                        )
                    }
                }
                // query gagal: jangan bocor ke viewModelScope (crash), lapor ke layar
                .catch {
                    updateState { state -> state.copy(isLoading = false) }
                    sendEffect(DetailTransactionEffect.ShowSnackbar("Transaksi gagal dimuat"))
                }
                .launchIn(viewModelScope)
    }

    /**
     * Hapus transaksi. Guard [DetailTransactionState.isDeleting] menahan tap kedua
     * supaya tidak delete dobel; kegagalan ditangkap lalu dilaporkan lewat
     * [DetailTransactionEffect.ShowSnackbar] (dibiarkan bocor ke viewModelScope = crash),
     * dialog ditutup supaya pengguna bisa mencoba lagi.
     */
    private fun handleDeleteTransaction(transactionId: String) {
        if (state.value.isDeleting) return

        updateState { it.copy(isDeleting = true) }

        viewModelScope.launch {
            val result = runCatching {
                transactionRepository.deleteTransaction(transactionId)
            }

            if (result.isSuccess) {
                sendEffect(DetailTransactionEffect.NavigateBack)
            } else {
                updateState { it.copy(isDeleting = false, deleteDialogState = null) }
                sendEffect(
                    DetailTransactionEffect.ShowSnackbar("Transaksi gagal dihapus. Coba lagi."),
                )
            }
        }
    }

    /** Salin transaksi ini jadi entri baru — id baru, isi & waktu sama. */
    private fun handleDuplicate() {
        val source = state.value.transaction ?: return

        viewModelScope.launch {
            val result = runCatching {
                transactionRepository.insertTransaction(
                    Transaction(
                        id = null,
                        title = source.name,
                        amount = source.amount,
                        categoryId = source.category?.id,
                        description = source.note,
                        typeTransaction = source.type,
                        accountId = source.account?.id,
                        dateTime = source.dateTime,
                    ),
                )
            }

            sendEffect(
                if (result.isSuccess) {
                    DetailTransactionEffect.ShowSnackbar("Transaksi disalin")
                } else {
                    DetailTransactionEffect.ShowSnackbar("Transaksi gagal disalin. Coba lagi.")
                },
            )
        }
    }

    /** Teks yang dibagikan tombol BAGIKAN — satu blok, siap ditempel ke chat. */
    private fun handleShare() {
        val transaction = state.value.transaction ?: return

        val shareText = buildString {
            appendLine("${transaction.name} • ${transaction.signedAmountText}")
            appendLine(
                "Tanggal: ${transaction.dateTime.formatDate()} ${transaction.dateTime.formatTime()}",
            )
            transaction.category?.name?.let { appendLine("Kategori: $it") }
            transaction.account?.name?.let { appendLine("${transaction.accountLabel}: $it") }
            transaction.note?.takeIf { it.isNotBlank() }?.let { appendLine("Catatan: $it") }
        }

        viewModelScope.launch {
            sendEffect(DetailTransactionEffect.ShareTransaction(shareText.trimEnd()))
        }
    }
}
