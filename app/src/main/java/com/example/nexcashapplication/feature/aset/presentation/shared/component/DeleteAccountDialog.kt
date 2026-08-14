package com.example.nexcashapplication.feature.aset.presentation.shared.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.feature.aset.domain.model.DeleteAccountDesision

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
                title = { Text("Tidak Dapat Dihapus") },
                text = { Text("Akun masih memiliki saldo Rp ${dialogDesision.balance.formatCompact()}. Kosongkan saldo terlebih dahulu.") },
                confirmButton = {
                    TextButton(onClick = { onDismiss() }) { Text("Tutup") }
                },
            )
        }

        is DeleteAccountDesision.ConfirmWithHistory -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Hapus Akun?") },
                text = { Text("${dialogDesision.transactionCount} transaksi terkait akan tetap tersimpan di riwayat.") },
                confirmButton = { TextButton(onClick = onConfirm) { Text("Hapus Akun") } },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
            )
        }

        DeleteAccountDesision.ConfirmSimple -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Hapus Akun?") },
                text = { Text("Akun akan dihapus secara permanen.") },
                confirmButton = { TextButton(onClick = onConfirm) { Text("Hapus Akun") } },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
            )
        }
    }
}