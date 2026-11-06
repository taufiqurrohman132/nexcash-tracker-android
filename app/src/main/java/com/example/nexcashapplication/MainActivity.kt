package com.example.nexcashapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.core.i18n.withAppLocale
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.notification.NotificationHelper
import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Preferensi tema dibaca di lapisan Activity, bukan lewat ViewModel, karena
     * `NexCashApplicationTheme` harus membacanya **sebelum** `NexcashApp` dikomposisi —
     * sedangkan semua ViewModel berada di dalam `NexcashApp`. Dibaca sebagai Flow biasa,
     * jadi pergantian warna langsung terlihat di seluruh app tanpa membuka ulang layar.
     */
    @Inject
    lateinit var settingsRepository: SettingsRepository

    /**
     * Tujuan dari notifikasi yang di-tap. State di Activity (bukan composable) supaya
     * `onNewIntent` saat aplikasi sudah berjalan tetap bisa mengisinya.
     */
    private val pendingDestination = mutableStateOf<Route?>(null)

    /**
     * Resources Activity harus mengikuti bahasa aplikasi (`values-in`, `values-en`, dst.).
     * Dibungkus di sini karena Application context tidak ikut ter-wrap oleh ini — cukup
     * Activity, tempat seluruh Compose UI membaca string resource.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withAppLocale())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingDestination.value = intent.toDestinationRoute()

        setContent {
            // DataStore gagal baca (file korup / disk penuh) → jangan crash; app tetap
            // jalan dengan tema dan warna bawaan. `initialValue` sudah AppSettings().
            val settingsFlow = remember(settingsRepository) {
                settingsRepository.settings.catch { e ->
                    Log.e(TAG, "gagal membaca pengaturan aplikasi", e)
                }
            }
            val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = AppSettings())

            NexCashApplicationTheme(
                darkTheme = when (settings.themeMode) {
                    AppThemeMode.LIGHT -> false
                    AppThemeMode.DARK -> true
                    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                },
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NexcashApp(
                        pendingDestination = pendingDestination.value,
                        onDestinationConsumed = { pendingDestination.value = null },
                    )
                }
            }
        }
    }

    /** Aplikasi sudah terbuka lalu notifikasi di-tap — launchMode singleTop masuk ke sini. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // supaya rekreasi Activity nanti memakai intent terbaru
        pendingDestination.value = intent.toDestinationRoute()
    }
}

/** Terjemahkan extra yang ditanam `NotificationHelper.post` menjadi route aplikasi. */
private fun Intent?.toDestinationRoute(): Route? =
    when (this?.getStringExtra(NotificationHelper.EXTRA_DESTINATION)) {
        NotificationHelper.DEST_NOTIFICATIONS -> Route.NotificationRoute.Main
        else -> null
    }
