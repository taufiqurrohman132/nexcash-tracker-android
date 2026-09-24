# Changelog

Semua perubahan penting pada proyek ini didokumentasikan di file ini.
Format mengikuti [Keep a Changelog](https://keepachangelog.com/id/1.1.0/),
versi mengikuti [Semantic Versioning](https://semver.org/lang/id/).

## [Unreleased]

### Ditambahkan

#### Fitur Anggaran Berulang (Recurring Budget)

- Model **series + instance**: satu baris `budgets` per periode, terhubung lewat
  `recurring_group_id`; root seri memakai **self-reference** (`recurring_group_id = id`)
  supaya unique index `(recurring_group_id, start_date)` benar-benar melindungi dari duplikat.
- `EnsureUpToDateBudgetsUseCase` — **lazy generation** periode berikutnya saat aplikasi dibuka
  (tanpa WorkManager), safety cap 24 periode/run, amount disalin dari baris terakhir.
- `SaveBudgetUseCase` — alur simpan create / update instance / ganti tipe periode, termasuk
  validasi (amount > 0, CUSTOM `end > start`) dan penutupan seri lama
  (hapus anak-duluan, potong baris membentang, matikan `is_recurring` root).
- `BudgetDao`: `getSeriesRoots()`, `getLastBudgetInSeries()`, `insertBudgetIfAbsent()`
  (`OnConflictStrategy.IGNORE`), query periode aktif distandardisasi
  (`start <= ref AND end >= ref`, urutan `start_date`).
- Domain `Budget.recurringGroupId` + helper `isSeriesRoot`.
- Repository: `getSeriesRoots()`, `getLastBudgetInSeries()`, `insertBudgetIfAbsent()`;
  seluruh metode `TODO()` pada `BudgetRepositoryImpl` diimplementasikan.
- **Hapus anggaran (tutup seri)** — `DeleteBudgetUseCase`: instance aktif + periode
  mendatang dihapus (anak-duluan demi FK RESTRICT), riwayat lampau tetap tersimpan
  dengan `is_recurring` root dimatikan, seri lain tidak tersentuh. Titik masuk:
  ikon hapus + dialog konfirmasi di `AddEditBudgetScreen` (TopAppBar) dan
  `EditPresentageBudgetScreen` (per kartu kategori).

#### Halaman Semua Transaksi (Lihat Semua)

- Satu layar + satu MVI untuk semua titik "Lihat Semua transaksi": rute
  `Route.TransactionRoute.All(filter)` dengan `TransactionFilter` sealed
  (`None`, `ByCategory`, `ByAccount`) — keputusan di
  [ADR-002](docs/adr/002-single-mvi-all-transactions.md).
- `GetTransactionsByFilterUseCase` — logika per-filter terpusat: judul TopAppBar
  diambil dari repository (ikut berubah saat entitas diedit), `ByCategory`
  menghitung subtree induk → subkategori (BFS) sehingga transaksi subkategori
  ikut terhitung di daftar induknya.
- Layar `feature/transaction/presentation/alltransactions` — grouping per tanggal
  + sticky header mengikuti tab Transaksi, lipat grup, loading, empty state,
  error + retry.
- Titik masuk: "Lihat Semua" di detail kategori → `ByCategory` (event
  `OnLoadMoreTransactionsClick` sebelumnya kosong) dan di detail akun →
  `ByAccount` (`onActionClick` sebelumnya mati).
- Query baru `getTransactionsByAccount` dan `getTransactionsWithDetailsByCategoryIds`
  (keduanya `deleted_at IS NULL`); paket lama
  `feature/category/presentation/transactionlist` (belum terpasang di NavHost dan
  membaca route yang salah) dihapus.

#### Dokumentasi

- `docs/adr/001-recurring-budget-series-instance.md` — Architecture Decision Records fitur recurring.
- `docs/adr/002-single-mvi-all-transactions.md` — keputusan satu MVI + filter di route
  untuk halaman Semua Transaksi.
- `docs/BUDGET_RECURRING.md` — spesifikasi aturan bisnis + diagram sequence (Mermaid).
- `CHANGELOG.md` ini.

#### Pengujian

- 23 unit test (JUnit Jupiter): mapper self-ref, generation (rollover, idempoten, cap,
  interval custom, weekly +1, one-shot), save logic (validasi, create, update, ganti tipe,
  urutan hapus FK), dan delete logic (tutup seri, riwayat aman, isolasi antar seri).
- **17 unit test fitur kategori** (`feature/category/presentation/main`):
  `CategoryViewModelTest` (load expense/income + budget aktif & terpakai pada window,
  event → state, event → effect, search dengan debounce 300ms via virtual time),
  `CategoryStateTest` (total/sisa anggaran per tab), `CategoryItemTest` (persentase),
  plus `FakeCategoryRepository` & `FakeTransactionRepository` in-memory.
- **7 unit test halaman Semua Transaksi** (`feature/transaction/domain/usecase`):
  `GetTransactionsByFilterUseCaseTest` — filter `None`/`ByAccount`, subtree subkategori
  3 level, reaktivitas subkategori baru, dan judul per filter, plus
  `FakeTransactionRepository`/`FakeAccountRepository` in-memory.
  Total suite: 47 test. Gradle: dependency `kotlinx-coroutines-test:1.11.0` dan
  `testOptions unitTests.returnDefaultValues` agar `android.util.Log` aman di JVM.

### Diubah

- **UI `AddEditBudgetScreen`**: teks durasi dihitung dinamis (bukan hardcode "12 Hari"),
  switch "Anggaran Berulang" hanya aktif untuk tipe `CUSTOM`, preview teks berulang
  disesuaikan per tipe periode, Snackbar effect terhubung (sebelumnya diabaikan),
  penanganan `ShowDiscardConfirmation` yang menyebabkan crash `TODO()` dihapus.
- `AddEditBudgetViewModel`: `handleSave()` memakai `SaveBudgetUseCase`, `ErrorShown -> TODO()`
  dihapus, form memuat instance aktif + sinkronisasi generasi saat dibuka.

### Diperbaiki

- **Migrasi Room v1 → v2 gagal validasi** (`Migration didn't properly handle: budgets`):
  skema v1 punya `recurring_group_id` bertipe `INTEGER` dengan FK `CASCADE`, sedangkan
  entity v2 mengharapkan `TEXT` dengan FK `RESTRICT` — keduanya tidak bisa di-ALTER
  in-place, sehingga `UPDATE` backfill saja tidak cukup. `MIGRATION_1_2` kini membangun
  ulang tabel `budgets`: deteksi kolom nyata via `PRAGMA table_info` (adaptif terhadap
  varian skema v1 lama), salin data ke staging table tanpa FK, drop + buat ulang tabel
  sesuai entity v2, backfill self-reference (`COALESCE(CAST(...), id)`), lalu buat ulang
  indeks dengan konvensi penamaan Room. Data budget pengguna tetap dipertahankan.

- **Grafik tren mingguan ("7 Hari Terakhir") di detail kategori**:

  1. **Marker salah nilai** — label saat titik disentuh menampilkan indeks sumbu-X
     (0–6) alih-alih nominal; kini menampilkan **nama hari + nilai Y** yang diambil
     dari `LineCartesianLayerMarkerTarget.points`.
  2. **Risiko crash sumbu bawah** — formatter bisa mengembalikan string kosong dan
     memicu `IllegalStateException` dari Vico (`check(it.isNotBlank)`); fallback
     kini selalu terisi dan formatter di-`remember` agar sumbu tidak dibuat ulang
     setiap recomposition.
  3. **Jendela tanggal meleset** — query SQL mencakup 8 hari (H-7..hari ini)
     sedangkan label hanya 7 slot (H-7..H-1), sehingga `GROUP BY dayOfWeek`
     menggabungkan transaksi hari ini dengan H-7 (weekday sama) — data hari ini
     bocor ke slot tertua dan hari ini tidak pernah tampil. Jendela kini tepat
     **H-6..hari ini (inklusif)**.
  4. **Data grafik ter-reset permanen** — `loadDetailCategory` membuat ulang
     `DetailCategoryUiModel` tanpa mempertahankan `weeklyData`/`weeklyLabels`;
     setiap re-emit Room kategori/subkategori mengosongkan grafik hingga transaksi
     berikutnya. Kini dipertahankan seperti `recentTransactions`.
  5. **Empty state tak pernah muncul** — seminggu tanpa transaksi (7 slot nol)
     menampilkan garis datar di dasar; kini tampil "Belum ada data transaksi
     minggu ini".
  6. **Keterbacaan label** — label sumbu/marker tanpa `fontSize` eksplisit (risiko
     teks terlalu kecil); kini 12sp, label marker diberi pill latar cyan dengan
     posisi `AroundPoint` + indikator titik, dan judul bagian mengikuti tipe
     kategori ("Tren Pemasukan" untuk pemasukan).

### Peringatan

- **Migrasi database Room v1 → v2** (otomatis, rebuild tabel `budgets`):

  1. `recurring_group_id`: `INTEGER (Long?)` → `TEXT (String?)`
  2. FK self-referencing: `ON DELETE CASCADE` → `ON DELETE RESTRICT`
  3. Backfill semua root lama (`NULL`) menjadi self-reference (`= id`)

  Versi skema dinaikkan di `NexCashDatabase` dan migrasi didaftarkan di
  `DatabaseModule` (`MIGRATION_1_2`). Karena tipe kolom dan aksi FK tak bisa diubah
  via `ALTER TABLE`, tabel `budgets` dibangun ulang lewat staging table (tanpa FK)
  — semua baris lama disalin, indeks dibuat ulang; tabel lain tidak tersentuh.
