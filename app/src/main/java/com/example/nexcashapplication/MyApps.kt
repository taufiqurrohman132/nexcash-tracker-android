package com.example.nexcashapplication

import android.app.Application
import android.util.Log
import com.example.nexcashapplication.feature.budgets.domain.usecase.EnsureUpToDateBudgetsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApps : Application() {

    @Inject
    lateinit var ensureUpToDateBudgets: EnsureUpToDateBudgetsUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Lazy generation: generate periode anggaran berikutnya saat aplikasi dibuka.
        // Flow Room akan re-emit begitu baris baru masuk, jadi UI ikut tersinkron.
        applicationScope.launch {
            runCatching { ensureUpToDateBudgets() }
                .onFailure { Log.e(TAG, "Gagal generate periode anggaran", it) }
        }
    }

    private companion object {
        const val TAG = "MyApps"
    }
}


