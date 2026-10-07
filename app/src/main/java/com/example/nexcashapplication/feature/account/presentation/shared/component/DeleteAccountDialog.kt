package com.example.nexcashapplication.feature.account.presentation.shared.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision

@Composable
fun DeleteAccountDialog(
    dialogDesision: DeleteAccountDesision,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    when (dialogDesision) {
        is DeleteAccountDesision.Blocked -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.acct_delete_blocked_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.acct_delete_blocked_message,
                            dialogDesision.balance.formatCompact(),
                        ),
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onDismiss() }) { Text(stringResource(R.string.acct_close)) }
                },
            )
        }

        is DeleteAccountDesision.ConfirmWithHistory -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.acct_delete_confirm_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.acct_delete_history_message,
                            dialogDesision.transactionCount,
                        ),
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirm) { Text(stringResource(R.string.acct_delete_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.acct_cancel)) }
                },
            )
        }

        DeleteAccountDesision.ConfirmSimple -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.acct_delete_confirm_title)) },
                text = { Text(stringResource(R.string.acct_delete_simple_message)) },
                confirmButton = {
                    TextButton(onClick = onConfirm) { Text(stringResource(R.string.acct_delete_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.acct_cancel)) }
                },
            )
        }
    }
}
