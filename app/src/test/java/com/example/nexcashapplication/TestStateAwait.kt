package com.example.nexcashapplication

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

/**
 * Tunggu sampai state sebuah ViewModel benar-benar mengendap.
 *
 * Kenapa `advanceUntilIdle()` saja tidak cukup: alur data sekarang memakai
 * `flowOn(Dispatchers.Default)`, jadi hasil pemetaan dikirim dari **thread
 * nyata** di luar scheduler virtual test. `advanceUntilIdle()` mengembalikan
 * kontrol sebelum hasil itu sampai ke `updateState` di Main.
 *
 * Strateginya: bolak-balik `advanceUntilIdle()` + jeda pendek sampai
 * `isLoading` false dan state tidak berubah lagi selama [QUIET_ROUNDS] putaran
 * (≈50 ms) — perubahan datangnya dari thread background, jadi jeda nyata memang
 * diperlukan.
 */
fun <S> TestScope.awaitSettled(
    state: () -> S,
    isLoading: (S) -> Boolean,
    timeoutMs: Long = 5_000,
) {
    val deadline = System.currentTimeMillis() + timeoutMs
    var last: S? = null
    var quiet = 0

    while (System.currentTimeMillis() < deadline) {
        advanceUntilIdle()
        val current = state()

        if (!isLoading(current)) {
            if (current == last) {
                quiet++
                if (quiet >= QUIET_ROUNDS) return
            } else {
                quiet = 0
                last = current
            }
        }
        Thread.sleep(10)
    }

    throw AssertionError(
        "state tidak mengendap dalam $timeoutMs ms " +
                "(isLoading=${isLoading(state())}, state=${state()})",
    )
}

private const val QUIET_ROUNDS = 5
