package com.example.nexcashapplication.feature.notification.domain.model

/**
 * Peringatan anggaran hasil penghitungan pemakaian — model domain, tanpa tampilan.
 *
 * Satu alert = satu budget yang melewati satu [threshold]. Sumber datanya turunan budget
 * (bukan tabel `notifications`), sehingga alert muncul/hilang mengikuti pemakaian aktual.
 *
 * @param id stabil antar-emisi Room (`"$budgetId-$threshold"`), dipakai sebagai kunci status baca
 *   di presentation supaya tidak reset tiap Room memancar ulang hasil yang sama.
 * @param budgetId id budget (sama dengan `Budget.id`) untuk navigasi ke layar budget.
 * @param percentUsed persen terpakai di periode berjalan, 0..100 (dibulatkan ke bawah).
 * @param threshold batas yang terlewati: 80 (hampir habis) atau 100 (tercapai).
 * @param periodLabel nama periode budget untuk pesan, mis. "Oktober".
 * @param periodStart awal periode budget (epoch millis). Bagian dari kunci anti-spam
 *   notifikasi — tanpa ini, "Oktober" 2026 dan 2027 dianggap sama sehingga alert tidak
 *   akan pernah dikirim ulang pada tahun berikutnya.
 */
data class BudgetAlert(
    val id: String,
    val budgetId: String,
    val categoryName: String,
    val percentUsed: Int,
    val threshold: Int,
    val periodLabel: String,
    val periodStart: Long,
)
