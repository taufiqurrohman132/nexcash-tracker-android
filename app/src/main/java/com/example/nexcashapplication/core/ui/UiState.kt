package com.example.nexcashapplication.core.ui

import com.example.nexcashapplication.core.i18n.UiText

/**
 * Status pemuatan **satu resource**.
 *
 * Aplikasi ini offline dan Room sudah reactive — flow mengemit sendiri begitu tabel
 * berubah. Karena itu tidak ada varian `isRefreshing`: tidak ada sumber remote yang
 * perlu ditarik ulang, jadi "sedang menyegarkan" bukan keadaan yang bisa terjadi.
 *
 * Pemakaian:
 * - Layar yang seluruh isinya **satu resource** yang diambil → satu [UiState] untuk
 *   keseluruhan state layar.
 * - Layar yang memuat **beberapa resource** dengan waktu tiba berbeda → satu [UiState]
 *   per resource, dan sisa state (query pencarian, tab, flag bottom sheet) tetap flat.
 *
 * Render dengan [Loadable] supaya keempat cabang tidak ada yang terlupakan.
 */
sealed class UiState<out T> {

    /**
     * Resource **belum diminta** — berbeda dari [Loading]. Dipakai untuk resource yang
     * dimuat lazily, mis. daftar kategori yang baru diambil saat sheet-nya dibuka;
     * tanpa varian ini sheet salah menampilkan indikator untuk data yang belum pernah
     * diminta, atau sebaliknya dianggap "tidak ada kategori".
     */
    data object Idle : UiState<Nothing>()

    /** Sudah diminta, emisi pertama belum tiba. */
    data object Loading : UiState<Nothing>()

    data class Success<out T : Any>(val data: T) : UiState<T>()

    /**
     * Gagal memuat. Membawa [UiText] (bukan [String] mentah) supaya pesannya bisa
     * diterjemahkan saat ditampilkan — lihat [com.example.nexcashapplication.core.i18n.UiText].
     *
     * [stale] = data lama yang tetap sah untuk ditampilkan **di belakang** pesan error.
     * Room gagal baca (disk penuh, file korup) tidak menghapus data yang pernah
     * tervalidasi, jadi layar menampilkan data lama + banner, bukan layar kosong.
     */
    data class Error<out T : Any>(
        val message: UiText,
        val stale: T? = null,
    ) : UiState<T>()
}

/**
 * Data yang sedang tampil, kalau ada: payload [UiState.Success], atau [UiState.Error.stale]
 * pada layar yang masih menampilkan data lama di belakang pesan errornya.
 *
 * `null` untuk [UiState.Idle] dan [UiState.Loading] — memang belum ada apa pun untuk ditampilkan.
 */
val <T : Any> UiState<T>.dataOrNull: T?
    get() = when (this) {
        is UiState.Success -> data
        is UiState.Error -> stale
        UiState.Idle, UiState.Loading -> null
    }
