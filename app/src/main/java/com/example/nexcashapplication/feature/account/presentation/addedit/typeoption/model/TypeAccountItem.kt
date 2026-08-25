package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel

// presentation/model/TypeAccountItem.kt
data class TypeAccountItem(
    val type: TypeAccount,
    val icon: ImageVector,
    val iconColor: Color,
    val listProviders: List<AccountProviderUiModel> = emptyList(), // diisi belakangan, bukan di mapper
)
