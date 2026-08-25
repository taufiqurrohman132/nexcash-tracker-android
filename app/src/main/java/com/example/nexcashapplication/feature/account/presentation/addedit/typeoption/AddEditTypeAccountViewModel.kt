package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.mapper.getAllTypeAccountItems
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditTypeAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : BaseViewModel<AddEditTypeAccountState, AddEditTypeAccountEvent, AddEditTypeAccountEffect>(
    initialState = AddEditTypeAccountState(),
) {

    init {
        updateState {
            it.copy(
                listTypeAccounts = getAllTypeAccountItems(),
            )
        }
    }

    override fun onEvent(event: AddEditTypeAccountEvent) {
        when (event) {
            AddEditTypeAccountEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(AddEditTypeAccountEffect.NavigateBack)
                }
            }

            AddEditTypeAccountEvent.OnNextClick -> {
                val type = state.value.typeAccountSelected ?: return
                viewModelScope.launch {
                    sendEffect(AddEditTypeAccountEffect.NavigateToAddEditInfoAccount(type))
                }
            }

            is AddEditTypeAccountEvent.OnTypeAccountSelected -> updateState {
                it.copy(
                    isNextEnabled = true,
                    typeAccountSelected = event.typeAccount,
                )
            }
        }
    }


}