# ADR-001: Budget Recurring dengan Model Series + Instance

- **Status**: Accepted
- **Tanggal**: 2026-09-24
- **Terdampak**: `feature/budgets`, `core/datasource/local` (BudgetDao, BudgetMapper, BudgetEntity), `BudgetRepository`, `MyApps`

## Context (Konteks)

NexCash perlu mendukung anggaran berulang dengan tipe periode `MONTHLY`, `WEEKLY`, dan `CUSTOM`:

- Periode berikutnya harus tersedia saat user membuka aplikasi (tanpa server).
- Edit limit di tengah periode tidak boleh mengubah riwayat, tapi periode berikutnya harus mewarisi nilai baru.
- Ganti tipe periode harus menutup seri lama dan memulai seri baru tanpa meninggalkan baris duplikat.
- Room memakai FK `recurring_group_id → id` dengan `onDelete = RESTRICT` dan unique index `(recurring_group_id, start_date)`.

## Decision (Keputusan)

### 1. Model series + instance (satu baris per periode)

Tabel `budgets` menyimpan **satu baris untuk setiap periode** (instance), yang dihubungkan oleh
`recurring_group_id` menjadi satu seri. Bukan satu baris dengan kolom `lastGeneratedDate`.

- **Alasan**: query periode aktif cukup `start <= today <= end` (sederhana, bisa di-index),
  riwayat edit per periode tersimpan natural, dan tidak perlu logika "proyeksi" saat membaca.
- **Konsekuensi**: jumlah baris tumbuh seiring waktu → dibatasi lazy generation (lihat butir 4).

### 2. Root seri memakai self-reference (`recurring_group_id = id`), bukan `NULL`

- **Alasan**: di SQLite, unique index **tidak** menjamin apa pun untuk nilai `NULL`
  (dua root `NULL` dengan `start_date` sama lolos). Dengan self-reference, root juga terlindungi
  index unik `(recurring_group_id, start_date)`.
- **Implementasi**: `BudgetMapper.toEntity()` → `recurringGroupId = recurringGroupId ?: rowId`.
  Hanya baris yang `recurring_group_id = id` yang dianggap root (`getSeriesRoots()`).
- **Konsekuensi**: data lama (v1, `NULL`) butuh migrasi → butir 9.

### 3. Insert generate memakai `IGNORE`, bukan `REPLACE`

`insertBudgetIfAbsent()` memakai `OnConflictStrategy.IGNORE`.

- **Alasan**: `REPLACE` = DELETE + INSERT. Jika baris root punya anak, DELETE root **dilanggar**
  FK `RESTRICT` → crash. `IGNORE` cukup karena duplikat periode memang berarti "sudah pernah digenerate".
- **Konsekuensi**: idempoten — aman dipanggil berulang tanpa baseline sync.

### 4. Lazy generation saat aplikasi dibuka (tanpa WorkManager), cap 24 periode/run

`EnsureUpToDateBudgetsUseCase` dipanggil dari `MyApps.onCreate()` (background scope).
Selama baris terakhir seri berakhir sebelum hari ini, generate periode berikutnya,
maksimal **24 iterasi per run**.

- **Alasan**: menghindari dependency WorkManager + kasus retry-nya sederhana (run berikutnya
  melanjutkan dari baris terakhir). Cap mencegah loop tak terbatas pada data sangat basi.
- **Konsekuensi**: periode baru telat beberapa detik setelah app dibuka; bila user tidak buka
  app > 2 tahun berturut-turut, sisa periode digenerate pada run berikutnya.

### 5. Start periode berikutnya selalu `end sebelumnya + 1 hari`

End dikomputasi per tipe, **start tidak pernah digeser mundur**:

| Tipe | Start | End |
|---|---|---|
| MONTHLY | `end + 1` | akhir bulan kalender dari start |
| WEEKLY | `end + 1` | `start + 6` hari |
| CUSTOM | `end + 1` | `start + interval` (durasi root) |

- **Alasan**: penyelarasan locale (`getWeeklyPeriodByLocale`) bisa menggeser start mundur →
  tumpang tindih dengan periode sebelumnya (terbukti pada unit test). Batas bulan saat *create*
  tetap kalender 1..akhir bulan; anchoring +1 hanya untuk *generate* (menangani root yang terpotong
  di tengah bulan).
- **Konsekuensi**: periode hasil generate tidak selalu rata pada hari yang sama (mis. weekly
  tetap 7 hari, tapi anchor mengikuti periode sebelumnya).

### 6. Edit limit di tengah periode → update instance aktif + periode mendatang

`SaveBudgetUseCase` (tipe sama): update baris instance yang sedang berjalan dan baris seri
yang `start >= start instance aktif`. Riwayat (`start < instance aktif`) tidak disentuh.
Periode yang belum digenerate otomatis meng-copy amount baris terakhir (butir 4).

- **Alasan**: riwayat adalah fakta tercatat; keputusan user berlaku ke depan.
- **Konsekuensi**: baris mendatang yang sudah ter-generate ikut di-update saat itu juga
  (konsisten dengan hasil generate berikutnya).

### 7. Ganti tipe periode → tutup seri lama, lalu buat root baru

1. Baris dengan `start >= windowStart` baru → **hapus**, urut `start DESC`
   (anak duluan, root terakhir → FK RESTRICT lolos).
2. Baris dengan `start < windowStart` tapi `end >= windowStart` → **potong** ke
   `windowStart - 1` + `is_recurring = false`.
3. Baris masa lalu (`end < windowStart`) → tidak disentuh.
4. Root seri yang tersentuh → `is_recurring = false`.
5. Insert root baru (self-reference) untuk window periode berjalan.

- **Konsekuensi**: baris anak di tengah yang terpotong tetap ada sebagai catatan periode parsial.

### 8. MONTHLY/WEEKLY selalu berulang; hanya CUSTOM yang menghormati switch

Switch "Anggaran Berulang" hanya aktif untuk `CUSTOM`. `CUSTOM` + switch mati = one-shot.

- **Alasan**: makna MONTHLY/WEEKLY memang berulang; mematikannya membuat tipe tersebut identik
  dengan CUSTOM.

### 9. Migrasi Room v1 → v2 (data-only)

```sql
UPDATE budgets SET recurring_group_id = id WHERE recurring_group_id IS NULL
```

- **Alasan**: mengubah root legacy `NULL` menjadi self-reference supaya proteksi index unik
  sejak awal berlaku. Tidak ada perubahan skema struktural → migrasi aman dan cepat.

## Consequences (Konsekuensi Ringkas)

- ✅ Tanpa background scheduler; alur baca tetap satu query sederhana.
- ✅ Insert idempoten; aman dari race run paralel (unique index menangkap).
- ✅ Riwayat anggaran tidak pernah dimutasi oleh edit.
- ⚠️ Periode baru muncul saat app dibuka (bisa telat hitungan detik).
- ⚠️ Cross-series duplikat untuk kategori yang sama dicegah oleh logika save
  (tutup seri lama dulu), bukan oleh index — lihat `SaveBudgetUseCase.closeSeries()`.

## Referensi

- Spesifikasi aturan bisnis: [`docs/BUDGET_RECURRING.md`](../BUDGET_RECURRING.md)
- Unit test: `app/src/test/java/com/example/nexcashapplication/feature/budgets/domain/usecase/`
  dan `core/datasource/local/mapper/BudgetMapperTest.kt`
