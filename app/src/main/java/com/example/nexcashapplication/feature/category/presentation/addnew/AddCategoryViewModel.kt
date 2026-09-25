package com.example.nexcashapplication.feature.category.presentation.addnew

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.datasource.local.datastore.PreferencesKeys
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect
import com.example.nexcashapplication.feature.category.presentation.mapper.toDomain
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiState
import com.example.nexcashapplication.feature.common.extentions.moveToFront
import com.example.nexcashapplication.feature.common.extentions.sortedColor
import com.example.nexcashapplication.feature.common.extentions.sortedIcon
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.common.extentions.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val categoryUseCases: CategoryUseCases,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddCategoryState, AddCategoryEvent, CategoryEffect>(AddCategoryState()) {

    private val route: Route.CategoryRoute.AddNew = savedStateHandle.toRoute()
    private val categoryId = route.categoryId
    private val parentId = route.parentId

    init {
        parentId?.let { pid ->
            viewModelScope.launch {
                categoryUseCases.getCategoryById(id = pid)
                        .collect { detailCategory ->
                            detailCategory?.let {
                                updateState {
                                    it.copy(
                                        parentCategory = ParentCategoryUi(
                                            id = detailCategory.id,
                                            name = detailCategory.name,
                                            icon = CategoryIcons.fromId(detailCategory.iconId),
                                            color = detailCategory.color.toColor(),
                                        ),
                                    )
                                }
                            }
                        }

            }
        }
        loadCategory(categoryId)

    }

    override fun onEvent(event: AddCategoryEvent) {
        when (event) {
            is AddCategoryEvent.OnParentCategorySelected -> {

            }

            is AddCategoryEvent.OnBackClicked -> {
                viewModelScope.launch {
                    sendEffect(CategoryEffect.NavigateBack)
                }
            }

            is AddCategoryEvent.OnSaveClicked -> {
                saveOrUpdate()

                viewModelScope.launch {
                    sendEffect(CategoryEffect.NavigateBack)
                }
            }

            is AddCategoryEvent.OnCancelClicked -> {

            }

            is AddCategoryEvent.OnNameChanged -> {
                updateState {
                    it.copy(name = event.name)
                }
            }


            is AddCategoryEvent.OnChangeIcon -> {
                val recentIcons = state.value.recentIcons.sortedIcon(event.icon, 8)

                updateState {
                    it.copy(
                        recentIcons = recentIcons,
                        selectedIcon = event.icon,
                    )
                }

                viewModelScope.launch {
                    saveRecentIcons(recentIcons.map { it.id })
                }

            }

            is AddCategoryEvent.OnColorSelected -> {
                val recentColors = state.value.recentColors.sortedColor(event.color, 4)

                updateState { addCategoryState ->
                    addCategoryState.copy(
                        selectedColor = event.color,
                        recentColors = recentColors,
                        showColorPalletDialog = false,
                    )
                }

                viewModelScope.launch {
                    saveRecentColors(recentColors.map { it.toHex() })
                }

            }

            is AddCategoryEvent.OnNotesChanged -> {
                updateState {
                    it.copy(note = event.notes)
                }
            }


            is AddCategoryEvent.OnTypeSelected -> {
                updateState {
                    it.copy(typeCategory = event.type)
                }
            }

            AddCategoryEvent.OnAddColorClicked -> {
                updateState {
                    it.copy(showColorPalletDialog = true)
                }
            }

            AddCategoryEvent.OnColorPickerDismiss -> {
                updateState {
                    it.copy(showColorPalletDialog = false)
                }
            }
        }

    }

    fun loadCategory(id: String?) {
        Log.d(TAG, "loadCategory: category id = $id")
        viewModelScope.launch {
            val recentColorsSnapshot = getRecentColorsSnapshot() //.take(4)
            Log.d(TAG, "loadCategory: awal recent color = $recentColorsSnapshot")
            // Pertama kali buka (pref masih kosong) → pakai daftar default,
            // supaya baris recent icons tidak pernah kosong.
            val recentIconsSnapshot = getRecentIconsSnapshot().ifEmpty { CategoryIcons.recentIcons }

            try {
                // edit mode
                if (id != null) {
                    categoryUseCases.getCategoryById(id = id)
                            .collect { detailCategory ->
                                if (detailCategory != null) {
                                    Log.d(TAG, "loadCategory: recent color = $recentColorsSnapshot")
                                    val newState = detailCategory.toUiState(
                                        recentColors = recentColorsSnapshot.moveToFront(
                                            color = detailCategory.color.toColor(), // sudah termasuk logic default dari toUiState()
                                            maxSize = 4,
                                        ),
                                        recentIcons = recentIconsSnapshot.moveToFront(
                                            icon = CategoryIcons.fromId(detailCategory.iconId),
                                            maxSize = 8,
                                        ),
                                    )
                                    updateState {
                                        newState
                                    }
                                }
                            }
                } else {
                    Log.d(TAG, "loadCategory: recent color else = $recentColorsSnapshot")
                    updateState {
                        // Mode tambah baru: recent icons juga wajib dimuat —
                        // dulu hanya warna yang diisi, baris icon selalu kosong.
                        val newState = it.copy(
                            recentColors = recentColorsSnapshot,
                            recentIcons = recentIconsSnapshot,
                        )
                        Log.d(TAG, "loadCategory: state after copy = ${newState.recentColors}")
                        newState
                    }
                }
            } catch (e: CancellationException) {
                Log.e(TAG, "loadCategory: $e")
                throw e // WAJIB rethrow, jangan ditelen
            } catch (e: Exception) {
                Log.e(TAG, "loadCategory: $e")
            }
        }
    }


    fun saveOrUpdate() {
        val currentState = state.value

        if (!currentState.isValid) {
            viewModelScope.launch {
                // effect error dll
            }
            return
        }

        viewModelScope.launch {
            try {
                // simpan
                val category = currentState.toDomain(id = currentState.id, parentId = parentId)

                Log.d(TAG, "saveOrUpdate: state id : ${currentState.id}")
                if (currentState.id.isBlank()) {
                    repository.insertCategory(category)
                } else {
                    Log.d(TAG, "saveOrUpdate: update")
                    repository.updateCategory(category)
                }
            } catch (e: Exception) {
                Log.e(TAG, "save: $e")
            }
        }
    }

    suspend fun saveRecentColors(colors: List<String>) {
        preferencesRepository.saveList(
            key = PreferencesKeys.RECENT_COLORS,
            value = colors,
            serializer = ListSerializer(String.serializer()),
        )
    }

    suspend fun saveRecentIcons(icons: List<String>) {
        preferencesRepository.saveList(
            key = PreferencesKeys.RECENT_ICONS,
            value = icons,
            serializer = ListSerializer(String.serializer()),
        )
    }

    suspend fun getRecentColorsSnapshot(): List<Color> =
        preferencesRepository
                .getList(PreferencesKeys.RECENT_COLORS, ListSerializer(String.serializer()))
                .map { hexList -> hexList.map { it.toColor() } }
                .first()

    suspend fun getRecentIconsSnapshot(): List<CategoryIcon> =
        preferencesRepository
                .getList(PreferencesKeys.RECENT_ICONS, ListSerializer(String.serializer()))
                .map { iconList -> iconList.map { CategoryIcons.fromId(it) } }
                .first()

    companion object {
        private val TAG = AddCategoryViewModel::class.java.simpleName
    }
}