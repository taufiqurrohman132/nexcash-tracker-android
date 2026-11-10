package com.example.nexcashapplication.core.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * Menghasilkan lambda callback [onClick] yang terlindungi dari ketukan ganda cepat (single-click / throttle guard).
 *
 * Klik pertama dieksekusi seketika (tanpa delay). Klik lanjutan yang terjadi dalam rentang [cooldownMs]
 * akan diabaikan secara aman.
 *
 * @param cooldownMs Waktu pendinginan dalam milidetik (default 600ms).
 * @param onClick Aksi yang ingin dijalankan saat tombol diklik.
 */
@Composable
fun rememberSingleClick(
    cooldownMs: Long = 600L,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= cooldownMs) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * Modifier [clickable] yang kebal terhadap double-click atau spam click yang tidak disengaja.
 *
 * @param cooldownMs Rentang waktu pengabaian klik berikutnya (default 600ms).
 * @param enabled Menentukan apakah elemen dapat diklik.
 * @param onClickLabel Label aksesibilitas untuk aksi klik.
 * @param role Peran semantik UI elemen (misal [Role.Button]).
 * @param onClick Aksi yang dieksekusi saat diklik.
 */
@Composable
fun Modifier.singleClickable(
    cooldownMs: Long = 600L,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier {
    val singleClick = rememberSingleClick(cooldownMs = cooldownMs, onClick = onClick)
    return this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = singleClick,
    )
}
