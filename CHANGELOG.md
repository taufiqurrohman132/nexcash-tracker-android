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

#### Dokumentasi

- `docs/adr/001-recurring-budget-series-instance.md` — Architecture Decision Records fitur recurring.
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
  Total suite: 40 test. Gradle: dependency `kotlinx-coroutines-test:1.11.0` dan
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

### Peringatan

- **Migrasi database Room v1 → v2** (otomatis, rebuild tabel `budgets`):

  1. `recurring_group_id`: `INTEGER (Long?)` → `TEXT (String?)`
  2. FK self-referencing: `ON DELETE CASCADE` → `ON DELETE RESTRICT`
  3. Backfill semua root lama (`NULL`) menjadi self-reference (`= id`)

  Versi skema dinaikkan di `NexCashDatabase` dan migrasi didaftarkan di
  `DatabaseModule` (`MIGRATION_1_2`). Karena tipe kolom dan aksi FK tak bisa diubah
  via `ALTER TABLE`, tabel `budgets` dibangun ulang lewat staging table (tanpa FK)
  — semua baris lama disalin, indeks dibuat ulang; tabel lain tidak tersentuh.
