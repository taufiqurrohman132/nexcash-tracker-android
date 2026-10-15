package com.example.nexcashapplication.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.asString

/**
 * Render satu [UiState] lewat empat cabang yang tidak bisa terlupakan.
 *
 * Memakai [Loadable] alih-alih menulis `when (state)` manual di tiap screen membuat
 * aturan tampil-error tetap di satu tempat: kalau besok keputusan "data lama tetap
 * tampil di belakang banner" berubah, cukup ubah di sini.
 *
 * **Tidak ada tombol "coba lagi" di sini, sengaja.** Aplikasi offline, dan `.catch`
 * menutup flow secara permanen — pemulihan dilakukan diam-diam oleh
 * `autoRetry(...)` di ViewModel, bukan oleh tombol yang menuntut user bertindak.
 *
 * [modifier] hanya dipakai pada cabang [UiState.Idle], [UiState.Loading] dan
 * [UiState.Error] tanpa data — pada cabang berisi data, layoutnya milik [content]
 * karena komposernya sendiri yang tahu bentuk layarnya.
 */
@Composable
fun <T : Any> Loadable(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    idle: @Composable () -> Unit = {},
    loading: @Composable () -> Unit = { CircularProgressIndicator() },
    error: @Composable (UiText) -> Unit = { LoadableErrorText(it) },
    errorBanner: @Composable (UiText) -> Unit = { LoadableErrorBanner(it) },
    content: @Composable (T) -> Unit,
) {
    when (state) {
        UiState.Idle -> Box(modifier) { idle() }

        UiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            loading()
        }

        is UiState.Success -> content(state.data)

        is UiState.Error -> {
            val stale = state.stale
            if (stale == null) {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    error(state.message)
                }
            } else {
                // Gagal baca ≠ data hangus: konten lama tetap di layar, pesan hanya
                // menumpang di atasnya sebagai banner.
                Box(modifier.fillMaxSize()) {
                    content(stale)
                    errorBanner(state.message)
                }
            }
        }
    }
}

/** Pesan error terpusat — dipakai ketika tidak ada data sama sekali untuk ditampilkan. */
@Composable
internal fun LoadableErrorText(message: UiText) {
    Text(
        text = message.asString(),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
    )
}

/** Banner error yang menumpang di atas data lama. */
@Composable
internal fun LoadableErrorBanner(message: UiText) {
    Surface(
        modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        tonalElevation = 2.dp,
    ) {
        Text(
            text = message.asString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}
