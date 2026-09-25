package com.example.nexcashapplication.feature.common.extentions

import com.example.nexcashapplication.core.domain.model.CategoryIcon


/** Pindahkan [icon] ke posisi paling depan (urut pemakaian terakhir), buang duplikat, lalu batasi [maxSize]. */
fun List<CategoryIcon>.moveToFront(icon: CategoryIcon, maxSize: Int? = null): List<CategoryIcon> {
    val combined = listOf(icon) + this.filterNot { it == icon }
    return if (maxSize != null) combined.take(maxSize) else combined
}

/**
 * Urutkan daftar recent icons berdasarkan pemakaian terakhir:
 * [icon] selalu dipindah ke depan — meski sudah ada di daftar —
 * duplikat dibuang, dan hasil dibatasi [limit].
 *
 * Dulu icon yang sudah ada tidak dipindah (cabang `contains → this`),
 * sehingga urutan recent tidak pernah berubah saat icon dipakai ulang.
 */
fun List<CategoryIcon>.sortedIcon(icon: CategoryIcon, limit: Int): List<CategoryIcon> =
        moveToFront(icon).take(limit)
