package com.example.nexcashapplication.feature.transaction.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.core.domain.model.TypeCategory
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
                        DetailTransactionEffect.ShowSnackbar(
                            uiText(R.string.tx_attachment_preview_unavailable),
                        ),
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
                    sendEffect(
                        DetailTransactionEffect.ShowSnackbar(
                            uiText(R.string.tx_detail_load_failed),
                        ),
                    )
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
                    DetailTransactionEffect.ShowSnackbar(
                        uiText(R.string.tx_delete_failed),
                    ),
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
                    DetailTransactionEffect.ShowSnackbar(uiText(R.string.tx_duplicated))
                } else {
                    DetailTransactionEffect.ShowSnackbar(
                        uiText(R.string.tx_duplicate_failed),
                    )
                },
            )
        }
    }

    /**
     * Teks yang dibagikan tombol BAGIKAN — satu blok, siap ditempel ke chat.
     * Per baris berupa [UiText] supaya labelnya (Tanggal, Kategori, Sumber Dana,
     * Catatan) di-resolve mengikuti bahasa aktif saat share sheet dibuka.
     */
    private fun handleShare() {
        val transaction = state.value.transaction ?: return

        val lines = buildList<UiText> {
            add(
                uiText(
                    R.string.tx_share_headline,
                    transaction.name,
                    transaction.signedAmountText,
                ),
            )
            add(
                uiText(
                    R.string.tx_share_date_line,
                    transaction.dateTime.formatDate(),
                    transaction.dateTime.formatTime(),
                ),
            )
            transaction.category?.name?.let { add(uiText(R.string.tx_share_category_line, it)) }
            transaction.account?.name?.let { name ->
                val lineRes = if (transaction.type == TypeCategory.EXPENSE) {
                    R.string.tx_share_funding_line
                } else {
                    R.string.tx_share_received_line
                }
                add(uiText(lineRes, name))
            }
            transaction.note?.takeIf { it.isNotBlank() }
                    ?.let { add(uiText(R.string.tx_share_note_line, it)) }
        }

        viewModelScope.launch {
            sendEffect(DetailTransactionEffect.ShareTransaction(lines))
        }
    }
}
