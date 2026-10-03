package com.example.nexcashapplication.feature.transaction.presentation.addnew

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.account.presentation.main.mapper.toAccountUiModel
import com.example.nexcashapplication.feature.common.utils.toPickedLocalDate
import com.example.nexcashapplication.feature.common.utils.withDate
import com.example.nexcashapplication.feature.common.utils.withTime
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper.toAddEditTransactionState
import com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel internal constructor(
    private val route: Route.TransactionRoute.AddEdit,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) : BaseViewModel<AddEditTransactionState, AddEditTransactionEvent, AddEditTransactionEffect>(
    initialState = AddEditTransactionState(),
) {

    /**
     * Konstruktor produksi (Hilt + Navigation): membaca typed route dari
     * [SavedStateHandle] lewat `toRoute()`.
     *
     * Catatan pengujian: `toRoute()` membaca argumen lewat Bundle Android; di JVM
     * unit test Bundle di-mock (`getString` selalu null). Unit test memakai
     * konstruktor `internal` dengan [Route.TransactionRoute.AddEdit] langsung —
     * jalur di device tetap identik lewat konstruktor ini.
     * Pola yang sama dipakai `DetailCategoryViewModel`.
     */
    @Inject constructor(
        savedStateHandle: SavedStateHandle,
        transactionRepository: TransactionRepository,
        categoryRepository: CategoryRepository,
        accountRepository: AccountRepository,
    ) : this(
        route = savedStateHandle.toRoute<Route.TransactionRoute.AddEdit>(),
        transactionRepository = transactionRepository,
        categoryRepository = categoryRepository,
        accountRepository = accountRepository,
    )

    private val transactionId = route.transactionId

    /**
     * Penanda alur cepat: dibuka lewat `OnCategoryClick(autoAdvance = true)`
     * (tombol Next dari field Nama), lalu dikonsumsi begitu kategori selesai
     * dipilih. Sengaja di luar state — ini info alur sesi, bukan data UI.
     */
    private var lanjutKeAccountSetelahKategori = false

    init {
        observeTransaction(transactionId)
    }


    override fun onEvent(event: AddEditTransactionEvent) {
        when (event) {

            is AddEditTransactionEvent.OnTimeClick -> {
                updateState {
                    it.copy(
                        showTimePicker = !it.showTimePicker,
                    )
                }
            }

            is AddEditTransactionEvent.OnSaveClick -> {
                handleSave()
            }

            is AddEditTransactionEvent.OnAccountClick -> {
                val willShow = !state.value.showAccountBottomSheet
                updateState {
                    it.copy(
                        showAccountBottomSheet = willShow,
                    )
                }

                if (willShow && state.value.availableAccounts.isEmpty()) {
                    viewModelScope.launch {
                        ensureAccountsLoaded()
                    }
                }
            }

            is AddEditTransactionEvent.OnAccountSelected -> {
                updateState {
                    it.copy(
                        selectedAccountId = event.account.id,
                        showAccountBottomSheet = false,
                    )
                }
            }

            is AddEditTransactionEvent.OnCategoryClick -> {
                val willShow = !state.value.showCategoryBottomSheet

                // flag cuma berlaku selama sheet Category terbuka: tutup manual
                // (scrim/tap lagi) = batal, jadi Account tidak ikut kebuka
                lanjutKeAccountSetelahKategori = event.autoAdvance && willShow

                updateState {
                    it.copy(
                        showCategoryBottomSheet = willShow,
                    )
                }

                if (willShow) {
                    viewModelScope.launch {
                        ensureCategoriesLoaded()
                    }
                }
            }

            is AddEditTransactionEvent.OnAttachmentClick -> {
                // Implement attachment logic
            }

            is AddEditTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(AddEditTransactionEffect.NavigateBack)
                }
            }

            is AddEditTransactionEvent.OnDateClick -> {
                updateState {
                    it.copy(
                        showDatePicker = !it.showDatePicker,
                    )
                }
            }

            is AddEditTransactionEvent.OnNoteChange -> {
                updateState {
                    it.copy(note = event.note)
                }
            }

            is AddEditTransactionEvent.OnRecurringChange -> {
                updateState {
                    it.copy(isRecurring = event.isRecurring)
                }
            }

            is AddEditTransactionEvent.OnTypeChange -> {
                updateState {
                    // kategori lama tidak sah untuk tipe baru → buang,
                    // supaya `isValid` menahan save sampai pilih ulang
                    val tipeBaru = if (event.type == TransactionType.EXPENSE) {
                        TypeCategory.EXPENSE
                    } else {
                        TypeCategory.INCOME
                    }
                    val masihValid = it.selectedCategoryId?.let { id ->
                        it.allCategories.any { group ->
                            group.categoriItem.tipe == tipeBaru && (
                                    group.categoriItem.id == id ||
                                            group.subCategoriesItem.any { sub -> sub.id == id }
                                    )
                        }
                    } ?: false

                    it.copy(
                        type = event.type,
                        selectedCategoryId = if (masihValid) it.selectedCategoryId else null,
                    )
                }
            }

            is AddEditTransactionEvent.OnNameTransactionChange -> {
                updateState {
                    it.copy(
                        nameTransaction = event.name,
                    )
                }
            }

            is AddEditTransactionEvent.OnAmountChange -> {
                updateState {
                    it.copy(
                        amount = event.amount.toLongOrNull() ?: 0,
                    )
                }
            }

            is AddEditTransactionEvent.OnDateChange -> {
                updateState {
                    // tanggal diganti, jam/menit lama dipertahankan
                    it.copy(
                        transactionAt = it.transactionAt.withDate(event.date.toPickedLocalDate()),
                    )
                }
            }

            is AddEditTransactionEvent.OnTimeChange -> {
                updateState {
                    // jam diganti, tanggal lama dipertahankan
                    it.copy(
                        transactionAt = it.transactionAt.withTime(event.hour, event.minute),
                    )
                }
            }

            is AddEditTransactionEvent.OnCategorySelected -> {
                val categoryId = event.category.id
                val parentCategory =
                    state.value.availableCategories.find { it.categoriItem.id == categoryId }
                val hasSubCategories = parentCategory?.subCategoriesItem?.isNotEmpty() == true

                // kategori selesai dipilih (sheet tertutup) & jalur cepat aktif
                // → langsung lanjut ke sheet Account. Kalau masih ada sub-kategori
                // sheet tetap terbuka, flag ditahan sampai sub-nya dipilih.
                val lanjutKeAccount = !hasSubCategories && lanjutKeAccountSetelahKategori
                if (lanjutKeAccount) lanjutKeAccountSetelahKategori = false

                updateState {
                    it.copy(
                        selectedCategoryId = categoryId,
                        showCategoryBottomSheet = hasSubCategories,
                        showAccountBottomSheet = if (lanjutKeAccount) {
                            true
                        } else {
                            it.showAccountBottomSheet
                        },
                    )
                }

                if (lanjutKeAccount) {
                    // di mode tambah, daftar akun belum tentu pernah dimuat
                    viewModelScope.launch {
                        ensureAccountsLoaded()
                    }
                }
            }

        }
    }

    /**
     * Simpan transaksi. Guard [AddEditTransactionState.isSaving] dipakai sebelum
     * launch coroutine supaya tap kedua tidak menghasilkan transaksi kembar;
     * kegagalan repository ditangkap lalu dilaporkan lewat [AddEditTransactionEffect.ShowSnackbar]
     * (kalau dibiarkan bocor ke viewModelScope, exception-nya crash).
     */
    private fun handleSave() {
        val currentState = state.value
        if (!currentState.isValid || currentState.isSaving) return

        updateState { it.copy(isSaving = true) }

        viewModelScope.launch {
            val transaction = Transaction(
                id = currentState.transactionId, // sesuaikan strategi ID-mu
                title = currentState.nameTransaction,
                amount = currentState.amount,
                categoryId = currentState.selectedCategoryId,
                description = currentState.note,
                typeTransaction = currentState.type,
                accountId = currentState.selectedAccountId,
                dateTime = currentState.transactionAt,
            )

            val result = runCatching {
                if (currentState.transactionId != null) {
                    transactionRepository.updateTransaction(transaction)
                } else {
                    transactionRepository.insertTransaction(transaction)
                }
            }

            updateState { it.copy(isSaving = false) }

            if (result.isSuccess) {
                sendEffect(AddEditTransactionEffect.NavigateBack)
            } else {
                sendEffect(
                    AddEditTransactionEffect.ShowSnackbar(
                        "Transaksi gagal disimpan. Coba lagi.",
                    ),
                )
            }
        }
    }

    private fun observeTransaction(id: String?) {
        viewModelScope.launch {
            if (id != null) {
                launch {
                    ensureCategoriesLoaded()
                }
                launch {
                    ensureAccountsLoaded()
                }

                transactionRepository.getTransactionWithDetailsById(id).collect { transaction ->
                    // sementara pakai let
                    transaction?.let {
                        updateState { current ->
                            transaction.toAddEditTransactionState(
                                allCategories = current.allCategories,
                                availableAccounts = current.availableAccounts,
                            ).copy(
                                showDatePicker = current.showDatePicker,
                                showTimePicker = current.showTimePicker,
                                showCategoryBottomSheet = current.showCategoryBottomSheet,
                                showAccountBottomSheet = current.showAccountBottomSheet,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun ensureCategoriesLoaded() {
        if (state.value.allCategories.isNotEmpty()) return

        runCatching {
            categoryRepository.getCategoriesAndSubCategories()
        }.onSuccess { result ->
            updateState {
                it.copy(allCategories = result.map { model -> model.toUIModel() })
            }
        }
    }

    private suspend fun ensureAccountsLoaded() {
        if (state.value.availableAccounts.isNotEmpty()) return

        runCatching {
            accountRepository.getAllAccounts().first()
        }.onSuccess { result ->
            updateState {
                it.copy(availableAccounts = result.map { it.toAccountUiModel() })
            }
        }
    }
}
