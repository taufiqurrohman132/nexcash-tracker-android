# ADR-002: Satu Layar + Satu MVI untuk Halaman Semua Transaksi dengan Filter di Route

- **Status**: Accepted
- **Tanggal**: 2026-09-24
- **Terdampak**: `core/navigations` (Route, NavigationStack), `feature/transaction` (domain + presentation/alltransactions), `feature/category` (DetailCategory*), `feature/account` (DetailAccountScreen)

## Context (Konteks)

Ada beberapa titik "Lihat Semua transaksi" dengan kebutuhan filter berbeda:

- **Detail kategori** → transaksi kategori tersebut **termasuk subkategori** turunannya.
- **Detail akun** → transaksi milik akun tersebut.
- **Mendatang** (dashboard/statistik) → semua transaksi, per bulan, per tipe, dsb.

Pertanyaannya: apakah setiap titik masuk perlu MVI (State/Event/Effect/ViewModel/Screen)
sendiri, atau cukup satu layar bersama?

Kondisi awal: `feature/category/presentation/transactionlist` sudah ada tetapi belum
terdaftar di NavHost, membaca route milik lain (`CategoryRoute.SubCategoryList`), tombol
"Lihat Semua" di `DetailAccountScreen` mati, dan `TransactionRepository` belum punya
query by-account.

## Decision (Keputusan)

### 1. Satu layar + satu MVI; perbedaan hanya `TransactionFilter` di route

```kotlin
@Serializable
data class All(val filter: TransactionFilter = TransactionFilter.None) : TransactionRoute

@Serializable
sealed interface TransactionFilter {
    data object None : TransactionFilter
    data class ByCategory(val categoryId: String) : TransactionFilter
    data class ByAccount(val accountId: String) : TransactionFilter
}
```

- **Alasan**: UI list, Event (`Back`/`TransactionClick`/`AddTransaction`/`GroupToggle`/`Retry`),
  dan Effect antar-varian **identik** — yang beda hanya query data + judul TopAppBar.
  MVI terpisah per entri berarti 5 file × N entri yang 80–90% copy-paste; setiap perbaikan
  bug harus diulang di N tempat dan test berlipat.
- **Konsekuensi**: State harus generik (tanpa field spesifik-per-filter). Varian filter baru =
  tambah varian sealed + satu cabang use case; layar & ViewModel tidak berubah.

### 2. Logika per-filter di domain (`GetTransactionsByFilterUseCase`), bukan di ViewModel

- **Alasan**: teruji langsung dengan fake repository tanpa `SavedStateHandle`/nav;
  ViewModel tetap tipis (baca route → delegasi → update state).
- **ByCategory**: subtree induk → anak dihitung **rekursif (BFS)** di Kotlin dari
  `getAllCategory()`, lalu dipakai sebagai query `IN (:ids)` — menutup celah transaksi
  subkategori tidak ikut terhitung di daftar induknya (masalah serupa dengan bug tren
  7 hari yang pernah ditemukan).
- **Judul di-fetch dari repository**, bukan argumen route → akurat dan ikut berubah
  saat entitas diedit di layar lain.

### 3. Package di `feature/transaction` — bukan di `feature/category`, bukan `feature/shared`

- **Aturan yang dipakai**: layar diletakkan di feature yang **memiliki data & route-nya**.
  Halaman ini menampilkan transaksi dan dijangkau lewat `Route.TransactionRoute` → milik
  `feature/transaction`. Feature lain cukup meng-import `core.navigations.Route`;
  mereka tidak perlu tahu package layarnya, sehingga tidak ada coupling antar feature.
- **Jangan membuat `feature/shared/` atau `feature/common/`** — tempat sampah yang
  lama-lama menjadi bola salju.

### 4. Kapan MVI boleh dipecah?

Saat salah satu varian punya **perilaku berbeda-beda nyata** (Event/State mulai beda —
mis. by-account punya kartu saldo + filter bulan + ekspor), pecah **hanya layar yang
beda** itu. Prinsipnya: pecah karena ada alasan fitur, bukan karena ragu.

## Consequences (Konsekuensi)

- **Positif**: DRY, perbaikan bug cukup satu tempat, test terpusat (7 unit test use case),
  penambahan filter murah.
- **Negatif**: satu ViewModel melayani banyak entri — jika kelak ada filter dengan
  kebutuhan UI sangat berbeda, layar itu harus dipecah (lihat butir 4).
- **Netral**: dua query baru (`getTransactionsByAccount`,
  `getTransactionsWithDetailsByCategoryIds`) keduanya konsisten `deleted_at IS NULL`;
  titik masuk lama dihapus (`transactionlist`) karena tidak terpasang dan salah route.
