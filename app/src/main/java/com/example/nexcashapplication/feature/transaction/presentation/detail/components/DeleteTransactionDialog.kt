package com.example.nexcashapplication.feature.transaction.presentation.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.i18n.asString
import com.example.nexcashapplication.feature.transaction.presentation.detail.state.DeleteTransactionDialogUiState

@Composable
fun DeleteTransactionDialog(
    deleteDialogUiState: DeleteTransactionDialogUiState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val balanceEffectText = deleteDialogUiState.balanceEffectText.asString()
    val typeLabel = stringResource(deleteDialogUiState.typeLabelRes)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tx_delete_title)) },
        text = {
            Column {
                Text(deleteDialogUiState.title, fontWeight = FontWeight.SemiBold)
                Text(
                    "$balanceEffectText · $typeLabel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    balanceEffectText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.tx_delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.tx_cancel)) }
        }
    )
}
