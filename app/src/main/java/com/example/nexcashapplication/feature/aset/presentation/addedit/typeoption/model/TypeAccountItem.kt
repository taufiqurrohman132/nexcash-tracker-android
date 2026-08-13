package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderItem
import com.example.nexcashapplication.feature.aset.presentation.main.model.TypeAccount

// presentation/model/TypeAccountItem.kt
data class TypeAccountItem(
    val type: TypeAccount,
    val icon: ImageVector,
    val iconColor: Color,
    val listProviders: List<AccountProviderItem> = emptyList(), // diisi belakangan, bukan di mapper
)
