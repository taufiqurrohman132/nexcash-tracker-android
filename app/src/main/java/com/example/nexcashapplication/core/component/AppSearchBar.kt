package com.example.nexcashapplication.core.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionEvent
import com.example.nexcashapplication.ui.theme.BackgroundLight
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextTertiary

@Composable
fun AppSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    placeholder: String,
    onSearchClear: () -> Unit,
    modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChange(it) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = {
                        onSearchClear()
                    },
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
        shape = RoundedCornerShape(50.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = PrimaryCyan,
            unfocusedIndicatorColor = TextTertiary,
            unfocusedContainerColor = BackgroundLight,
            focusedContainerColor = BackgroundLight,
        ),
    )
}