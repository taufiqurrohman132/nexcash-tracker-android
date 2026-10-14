package com.example.nexcashapplication.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel MVI dasar: satu [StateFlow] untuk state, satu [Channel] untuk effect satu-arah.
 *
 * ## Konvensi penanganan error di project ini
 *
 * Error ditangkap di lapisan TERAKHIR yang bisa menerjemahkannya menjadi sesuatu yang
 * dimengerti UI — yaitu ViewModel:
 *
 * - **Baca (Flow)**: setiap koleksi yang membaca **sumber data** (Room, DataStore, dsb.)
 *   wajib punya `catch {}` sebelum `collect`/`launchIn` supaya kegagalan baca tidak bocor
 *   ke `viewModelScope` = crash. Flow yang murni **in-memory** (`state.map { ... }.collect`,
 *   pencarian di atas state sendiri) tidak punya sumber kegagalan, jadi `catch` di sana
 *   hanya dead code — tidak perlu dipasang.
 * - **Tulis (suspend)**: bungkus dengan [runCancellable] (atau `try`/`catch` yang melempar
 *   ulang `CancellationException`), lalu terjemahkan ke `UiText` via
 *   [com.example.nexcashapplication.core.i18n.toUiText] dan kirim lewat effect snackbar.
 *
 * Repository sengaja TIDAK menangkap error secara global — lihat komentar di
 * `core/datasource/repository`. Use case hanya membungkus *business failure* ke `Result`.
 */
abstract class BaseViewModel<State, Event, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    abstract fun onEvent(event: Event)

    protected fun updateState(update: (State) -> State) {
        _state.update(update)
    }

    protected suspend fun sendEffect(effect: Effect) {
        _effect.send(effect)
    }

    /**
     * [runCatching] yang **meneruskan** [CancellationException].
     *
     * `runCatching` biasa menelan pembatalan coroutine (mis. ViewModel di-cleared) sebagai
     * `Result.failure`, sehingga blok `onFailure` jalan di scope yang sudah mati — bug klasik
     * yang bikin "error" palsu muncul saat layar ditutup. Helper ini melempar ulang
     * pembatalan supaya hanya kegagalan nyata yang menjadi `Result.failure`.
     */
    protected suspend fun <T> runCancellable(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
