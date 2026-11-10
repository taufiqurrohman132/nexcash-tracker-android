package com.example.nexcashapplication.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan

/**
 * Host snackbar aplikasi NexCash dengan styling kustom terpusat (Option 1).
 * Menggunakan Material 3 Snackbar dengan penyesuaian bentuk rounded corner,
 * warna latar yang kontras dan nyaman di mata, serta warna aksi yang sesuai tema.
 *
 * Contoh penggunaan pada Scaffold:
 * ```kotlin
 * Scaffold(
 *     snackbarHost = { AppSnackbarHost(snackbarHostState) },
 *     ...
 * )
 * ```
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    actionColor: Color = PrimaryCyan,
    actionContentColor: Color = PrimaryCyan,
    dismissActionContentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        AppSnackbar(
            snackbarData = data,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = actionColor,
            actionContentColor = actionContentColor,
            dismissActionContentColor = dismissActionContentColor,
        )
    }
}

/**
 * Komponen Snackbar tunggal dengan styling konsisten.
 */
@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    actionColor: Color = PrimaryCyan,
    actionContentColor: Color = PrimaryCyan,
    dismissActionContentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionColor = actionColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
    )
}

// =========================================================================
// PREVIEWS
// =========================================================================

private class PreviewSnackbarData(
    override val visuals: SnackbarVisuals,
) : SnackbarData {
    override fun dismiss() {}
    override fun performAction() {}
}

private class SimpleSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

@Preview(showBackground = true)
@Composable
private fun AppSnackbarPreview() {
    NexCashApplicationTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Snackbar biasa
            AppSnackbar(
                snackbarData = PreviewSnackbarData(
                    SimpleSnackbarVisuals(message = "Transaksi berhasil disimpan")
                )
            )

            // Snackbar dengan tombol aksi
            AppSnackbar(
                snackbarData = PreviewSnackbarData(
                    SimpleSnackbarVisuals(
                        message = "Kategori berhasil dihapus",
                        actionLabel = "URUNGKAN",
                        withDismissAction = true
                    )
                )
            )

            // Snackbar error custom
            AppSnackbar(
                snackbarData = PreviewSnackbarData(
                    SimpleSnackbarVisuals(
                        message = "Gagal memuat data dari server",
                        actionLabel = "COBA LAGI"
                    )
                ),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionColor = MaterialTheme.colorScheme.error,
                actionContentColor = MaterialTheme.colorScheme.error
            )
        }
    }
}
