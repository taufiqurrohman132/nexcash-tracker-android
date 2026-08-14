package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

import androidx.compose.runtime.Immutable
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.model.TypeAccountItem

@Immutable
data class AddEditTypeAccountState(
    val isLoading: Boolean = false,
    val typeAccountSelected: TypeAccount? = null,
    val isNextEnabled: Boolean = false,
    val listTypeAccounts: List<TypeAccountItem> = emptyList(),
    val error: String? = null,
)