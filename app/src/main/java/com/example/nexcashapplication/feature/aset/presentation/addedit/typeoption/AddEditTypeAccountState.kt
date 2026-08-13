package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.model.TypeAccountItem
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount

@Immutable
data class AddEditTypeAccountState(
    val isLoading: Boolean = false,
    val typeAccountSelected: TypeAccount? = null,
    val isNextEnabled: Boolean = false,
    val listTypeAccounts: List<TypeAccountItem> = emptyList(),
    val error: String? = null,
)