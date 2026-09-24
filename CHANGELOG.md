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

#### Dokumentasi

- `docs/adr/001-recurring-budget-series-instance.md` — Architecture Decision Records fitur recurring.
- `docs/BUDGET_RECURRING.md` — spesifikasi aturan bisnis + diagram sequence (Mermaid).
- `CHANGELOG.md` ini.

#### Pengujian

- 18 unit test (JUnit Jupiter): mapper self-ref, generation (rollover, idempoten, cap,
  interval custom, weekly +1, one-shot), save logic (validasi, create, update, ganti tipe,
  urutan hapus FK).

### Diubah

- **UI `AddEditBudgetScreen`**: teks durasi dihitung dinamis (bukan hardcode "12 Hari"),
  switch "Anggaran Berulang" hanya aktif untuk tipe `CUSTOM`, preview teks berulang
  disesuaikan per tipe periode, Snackbar effect terhubung (sebelumnya diabaikan),
  penanganan `ShowDiscardConfirmation` yang menyebabkan crash `TODO()` dihapus.
- `AddEditBudgetViewModel`: `handleSave()` memakai `SaveBudgetUseCase`, `ErrorShown -> TODO()`
  dihapus, form memuat instance aktif + sinkronisasi generasi saat dibuka.

### Peringatan

- **Migrasi database Room v1 → v2** (otomatis, data-only):

  ```sql
  UPDATE budgets SET recurring_group_id = id WHERE recurring_group_id IS NULL
  ```

  Versi skema dinaikkan di `NexCashDatabase` dan migrasi didaftarkan di
  `DatabaseModule` (`MIGRATION_1_2`). Pengguna v1 akan dimigrasi saat pertama membuka
  versi baru; tidak ada kolom yang ditambah/dihapus.
