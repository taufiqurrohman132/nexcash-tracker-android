package com.example.nexcashapplication.core.i18n

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Teks tampilan yang **belum di-resolve** ke [String].
 *
 * Dibutuhkan karena kode non-UI (ViewModel, use case, mapper, state) tidak boleh membawa
 * `Context` — selain arsitektur, unit test membangun kelas-kelas itu langsung tanpa Android,
 * jadi `context.getString()` akan gagal di test JVM. Dengan [UiText], yang dibawa hanyalah
 * **kunci resource + argumennya**, sehingga teksnya baru diterjemahkan saat ditampilkan dan
 * selalu mengikuti bahasa yang sedang aktif.
 *
 * Contoh di ViewModel:
 * ```kotlin
 * sendEffect(ShowSnackbar(uiText(R.string.budget_save_failed, nominal)))
 * ```
 * dan di composable:
 * ```kotlin
 * is ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.asString())
 * ```
 *
 * Untuk label statis (enum/mapper) yang tidak punya argumen, cukup `@StringRes Int` biasa —
 * lihat pola `TypeAccount.labelResId` — dan resolve dengan `stringResource(...)`.
 */
sealed interface UiText {

    /** Teks dari string resource, siap diformat dengan [args] (`%1$s`, `%1$d`, ...). */
    data class Res(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText

    /** Teks mentah yang memang tidak bisa diterjemahkan, mis. pesan exception teknis. */
    data class Raw(val value: String) : UiText
}

/** [UiText.Res] vararg: `uiText(R.string.tx_not_found, id)`. */
fun uiText(@StringRes resId: Int, vararg args: Any): UiText = UiText.Res(resId, args.toList())

/**
 * Pesan dari [Throwable]: pakai teks teknisnya apa adanya kalau ada dan tidak kosong,
 * kalau tidak jatuh ke [fallback] (string resource milik fitur pemanggil).
 */
fun Throwable.toUiText(@StringRes fallback: Int): UiText =
    message?.takeIf { it.isNotBlank() }?.let { UiText.Raw(it) } ?: UiText.Res(fallback)

/**
 * Resolve ke [String] — **hanya dari composable**, karena membaca Resources
 * (Resources itulah yang ikut berbahasa saat Activity di-recreate).
 */
@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Res -> stringResource(resId, *args.toTypedArray())
    is UiText.Raw -> value
}
