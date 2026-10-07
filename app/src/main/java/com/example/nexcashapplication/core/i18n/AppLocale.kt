package com.example.nexcashapplication.core.i18n

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

/**
 * Sumber tunggal bahasa aplikasi.
 *
 * Kenapa perlu object begini: locale harus sudah benar **sebelum** Activity pertama dibuat
 * (`Locale.getDefault()` dipakai `java.time`, `NumberFormat`, dan formatter proyek), sementara
 * bahasa disimpan di DataStore yang dibaca secara async. [MyApps.onCreate] membaca DataStore
 * (blocking, sekali per start proses) lalu memanggil [apply] — sehingga [MainActivity.attachBaseContext]
 * dan seluruh non-UI code sudah melihat bahasa yang tepat.
 *
 * Ganti bahasa di layar Settings cukup memanggil [apply] lagi; layar lalu melakukan
 * `Activity.recreate()` supaya resources ikut berubah.
 */
object AppLocale {

    @Volatile
    private var current: Locale = Locale.getDefault()

    /** Bahasa aplikasi yang sedang aktif. */
    val locale: Locale
        get() = current

    /** Terapkan bahasa dari BCP-47 tag, mis. `"id"`, `"en"`, `"pt"`. */
    fun apply(tag: String) = apply(Locale.forLanguageTag(tag))

    /**
     * Terapkan [locale]: set default JVM (`Locale.setDefault`) supaya `java.time` /
     * `NumberFormat` ikut, dan set [LocaleList] default untuk API 24+.
     */
    fun apply(locale: Locale) {
        current = locale
        Locale.setDefault(locale)
        LocaleList.setDefault(LocaleList(locale))
    }
}

/**
 * Context baru yang resources-nya mengikuti [AppLocale.locale].
 *
 * Dipakai untuk konteks **non-Activity** (Application, Worker, NotificationHelper): context
 * Activity sudah ter-wrap di `MainActivity.attachBaseContext`, tetapi Application context tidak,
 * jadi `context.getString()` dari background worker tetap memakai bahasa perangkat.
 */
fun Context.withAppLocale(): Context {
    val config = Configuration(resources.configuration)
    config.setLocales(LocaleList(AppLocale.locale))
    return createConfigurationContext(config)
}
