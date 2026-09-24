package com.example.nexcashapplication.feature.budgets.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.EnsureUpToDateBudgetsUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.SaveBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
    private val saveBudgetUseCase: SaveBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val ensureUpToDateBudgets: EnsureUpToDateBudgetsUseCase,
) : BaseViewModel<AddEditBudgetState, AddEditBudgetEvent, AddEditBudgetEffect>(
    AddEditBudgetState(),
) {
    private val route: Route.BudgetRoute.AddEdit = savedStateHandle.toRoute()
    private val budgetId = route.budgetId
    private val categoryId = route.categoryId

    init {
        loadData()
    }

    override fun onEvent(event: AddEditBudgetEvent) {
        when (event) {
            AddEditBudgetEvent.BatalClicked -> {
                viewModelScope.launch {
                    sendEffect(
                        AddEditBudgetEffect.NavigateBack,
                    )
                }
            }

            is AddEditBudgetEvent.BatasBaruChanged -> {
                updateState {
                    it.copy(
                        batasBaruParsed = event.rawInput.toLongOrNull() ?: 0L,
                    )
                }
            }

            is AddEditBudgetEvent.BerulangToggled -> {
                updateState {
                    // Switch hanya aktif di CUSTOM; tipe lain selalu "berulang".
                    if (it.isBerulangToggleEnabled) it.copy(isBerulang = event.enabled) else it
                }
            }

            AddEditBudgetEvent.ErrorShown -> updateState { it.copy(errorMessage = null) }

            is AddEditBudgetEvent.PeriodeTipeChanged -> updateState { st ->
                val tipe = event.tipe
                val today = LocalDate.now()
                val (mulai, akhir) = if (tipe == RecurrenceType.CUSTOM) {
                    st.tanggalMulai to st.tanggalBerakhir // pertahankan pilihan user, bisa diedit
                } else {
                    // Non-custom: field tanggal non-editable → snap ke periode yang mencakup hari ini.
                    getBudgetPeriodUseCase(
                        isRecurring = true,
                        type = tipe,
                        startDate = today,
                        endDate = null,
                    )
                }
                st.copy(
                    periodeTipe = tipe,
                    tanggalMulai = mulai,
                    tanggalBerakhir = akhir,
                    isTanggalEditable = tipe == RecurrenceType.CUSTOM,
                    isBerulangToggleEnabled = tipe == RecurrenceType.CUSTOM,
                    isBerulang = if (tipe == RecurrenceType.CUSTOM) st.isBerulang else true,
                )
            }

            AddEditBudgetEvent.SimpanClicked -> {
                viewModelScope.launch {
                    handleSave()
                }
            }

            AddEditBudgetEvent.HapusClicked ->
                // Trash hanya ditampilkan saat budget sudah ada (state.budgetId != null)
                if (state.value.budgetId != null) {
                    updateState { it.copy(showDeleteConfirmation = true) }
                }

            AddEditBudgetEvent.BatalHapusClicked ->
                updateState { it.copy(showDeleteConfirmation = false) }

            AddEditBudgetEvent.KonfirmasiHapusClicked -> {
                viewModelScope.launch {
                    handleDelete()
                }
            }

            is AddEditBudgetEvent.TanggalBerakhirChanged -> {
                updateState {
                    it.copy(
                        tanggalBerakhir = event.date,
                        tanggalMulai = if (event.date <= it.tanggalMulai) event.date.minusDays(1) else it.tanggalMulai,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiChanged -> {
                updateState {
                    it.copy(
                        tanggalMulai = event.date,
                        tanggalBerakhir = if (event.date >= it.tanggalBerakhir) event.date.plusDays(
                            1,
                        ) else it.tanggalBerakhir,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalBerakhirClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = DateFieldTarget.TanggalAkhir,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = DateFieldTarget.TanggalMulai,
                    )
                }
            }

            AddEditBudgetEvent.TanggalDismissClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = null,
                    )
                }
            }
        }
    }

    private suspend fun handleSave() {
        val st = state.value
        updateState { it.copy(isSaving = true) }

        val result = saveBudgetUseCase(
            SaveBudgetUseCase.Request(
                budgetId = budgetId,
                categoryId = categoryId,
                amount = st.batasBaruParsed,
                periodType = st.periodeTipe,
                customStart = st.tanggalMulai,
                customEnd = st.tanggalBerakhir,
                isRecurringRequested = st.isBerulang,
            ),
        )

        updateState { it.copy(isSaving = false) }
        when (result) {
            is SaveBudgetUseCase.Result.Success -> sendEffect(AddEditBudgetEffect.NavigateBack)
            is SaveBudgetUseCase.Result.Failure ->
                sendEffect(AddEditBudgetEffect.ShowSnackbar(result.message))
        }
    }

    private suspend fun handleDelete() {
        val st = state.value
        val id = st.budgetId
        if (id == null) {
            updateState { it.copy(showDeleteConfirmation = false) }
            return
        }

        updateState { it.copy(isSaving = true) }
        val existing = budgetRepository.getById(id)
        val result = if (existing == null) {
            DeleteBudgetUseCase.Result.Failure("Anggaran tidak ditemukan")
        } else {
            deleteBudgetUseCase(existing)
        }
        updateState { it.copy(isSaving = false, showDeleteConfirmation = false) }

        when (result) {
            is DeleteBudgetUseCase.Result.Success ->
                sendEffect(AddEditBudgetEffect.NavigateBack)

            is DeleteBudgetUseCase.Result.Failure ->
                sendEffect(AddEditBudgetEffect.ShowSnackbar(result.message))
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Sinkronkan periode dulu supaya instance aktif hari ini tersedia.
            runCatching { ensureUpToDateBudgets() }

            categoryId?.let { catId ->
                categoryRepository.getCategoryById(catId).first()?.let { cat ->
                    updateState { it.copy(kategoriNama = cat.name, kategoriIkon = cat.iconId) }
                }
            }

            val today = LocalDate.now()
            val existing = budgetId?.let { budgetRepository.getById(it) }
                ?: categoryId?.let { catId ->
                    budgetRepository.getActiveBudgetForCategory(catId, today).first()
                }

            if (existing == null) {
                // Mode buat baru: default state (MONTHLY, hari ini) sudah sesuai.
                updateState { it.copy(isLoading = false) }
                return@launch
            }

            updateState {
                it.copy(
                    budgetId = existing.id,
                    categoryId = existing.categoryId,
                    periodeTipe = existing.recurrenceType,
                    tanggalMulai = existing.startDate,
                    tanggalBerakhir = existing.endDate,
                    isTanggalEditable = existing.recurrenceType == RecurrenceType.CUSTOM,
                    isBerulangToggleEnabled = existing.recurrenceType == RecurrenceType.CUSTOM,
                    isBerulang =
                        if (existing.recurrenceType == RecurrenceType.CUSTOM) existing.isRecurring
                        else true,
                    batasSebelumnya = existing.amount,
                    batasBaruParsed = existing.amount,
                    isLoading = false,
                )
            }
        }
    }
}
