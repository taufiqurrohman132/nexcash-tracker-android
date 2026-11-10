# NexCash Application

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-6650a4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-MVVM_+_Clean-blue" />
  <img src="https://img.shields.io/badge/Language-Kotlin-orange?logo=kotlin" />
  <img src="https://img.shields.io/badge/Version-1.0-brightgreen" />
</p>

**NexCash** adalah aplikasi manajemen keuangan pribadi modern yang dibangun dengan **Jetpack Compose** dan **Material 3**. Membantu pengguna melacak pengeluaran, mengelola anggaran, dan mengorganisir transaksi keuangan berdasarkan kategori.

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|---|---|
| 📊 **Dashboard** | Ringkasan keuangan bulanan, grafik pemasukan/pengeluaran, aksi cepat |
| 💸 **Transaksi** | Tambah, edit, hapus, duplikasi transaksi dengan kategori & akun sumber |
| 🏦 **Akun & Aset** | Kelola beberapa akun (tunai, tabungan, e-wallet, dsb.) |
| 📁 **Kategori** | Kategori & sub-kategori bertingkat dengan ikon dan warna kustom |
| 🎯 **Anggaran** | Anggaran per kategori dengan dukungan **recurring** (bulanan/mingguan/kustom) |
| 📈 **Statistik** | Donut chart interaktif, perbandingan alokasi vs realisasi |
| 🔔 **Notifikasi** | Notifikasi keuangan dengan mark-as-read |
| ⚙️ **Pengaturan** | Mata uang, bahasa, preferensi tampilan |

---

## 🛠️ Tech Stack

| Layer | Library / Tool |
|---|---|
| **UI** | Jetpack Compose, Material 3, `ConstraintLayout` |
| **Navigasi** | Navigation Compose |
| **State** | ViewModel, `StateFlow`, `collectAsStateWithLifecycle` |
| **DI** | Hilt |
| **Database** | Room (SQLite) |
| **Arsitektur** | MVVM + Clean Architecture (UI → Domain → Data) |
| **Chart** | Vico |
| **Async** | Kotlin Coroutines + Flow |
| **Testing** | JUnit 4, Mockito, `kotlinx-coroutines-test` |

---

## 📁 Struktur Proyek

```
app/src/main/java/com/example/nexcashapplication/
├── core/
│   ├── component/        # Komponen UI reusable (AppButton, AppSnackbar, MonthPicker, dsb.)
│   ├── domain/           # Model domain & base use case
│   ├── extension/        # Kotlin extension (ClickExtension - throttle guard)
│   ├── currency/         # Format & konversi mata uang
│   └── i18n/             # Lokalisasi & UiText
│
└── feature/
    ├── account/          # Manajemen akun & aset
    ├── budgets/          # Anggaran (termasuk recurring)
    ├── category/         # Kategori & sub-kategori
    ├── dashboard/        # Layar utama & ringkasan
    ├── insight/          # Insight keuangan
    ├── notification/     # Notifikasi
    ├── settings/         # Pengaturan aplikasi
    ├── statistics/       # Statistik & grafik
    └── transaction/      # CRUD transaksi & semua tampilan transaksi
```

---

## 🚀 Memulai

Lihat [**Setup Guide**](docs/SETUP.md) untuk instruksi lengkap clone dan menjalankan proyek.

**Persyaratan singkat:**
- Android Studio Hedgehog atau lebih baru
- JDK 17
- Android SDK 24+

```bash
git clone https://github.com/taufiqurrohman132/nexcash-tracker-android.git
cd nexcash-tracker-android
./gradlew assembleDebug
```

---

## 📖 Dokumentasi

| Dokumen | Deskripsi |
|---|---|
| [Architecture & Tech Stack](docs/ARCHITECTURE.md) | Clean Architecture, layer, dan library |
| [Setup & Environment](docs/SETUP.md) | Prasyarat dan instruksi build |
| [Code Guide](docs/CODE_GUIDE.md) | Struktur direktori & panduan komponen kunci |
| [Testing Strategy](docs/TESTING.md) | Unit test dan UI test |
| [Contribution Guidelines](docs/CONTRIBUTING.md) | Standar kode dan Git flow |
| [Git Workflow](docs/GIT_WORKFLOW.md) | Branching strategy dan conventional commits |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Solusi masalah build dan runtime umum |
| [Spesifikasi Anggaran Berulang](docs/BUDGET_RECURRING.md) | Aturan bisnis recurring budget + sequence diagram |
| [ADR: Recurring Budget](docs/adr/001-recurring-budget-series-instance.md) | Keputusan arsitektur series + instance |
| [Changelog](CHANGELOG.md) | Riwayat perubahan tiap rilis |

---

## 🏗️ Arsitektur

Proyek mengikuti **Clean Architecture** dengan 3 layer utama:

```
UI Layer (Compose Screens + ViewModel)
        ↓  UiState / UiEvent
Domain Layer (Use Cases + Domain Models)
        ↓  Repository Interface
Data Layer (Room DAO + Repository Impl)
```

Setiap fitur berdiri mandiri (*feature-first*) dengan struktur `presentation/`, `domain/`, dan `data/` di dalam masing-masing modul fitur.

---

## 🧪 Testing

Unit test tersedia untuk:
- **ViewModel** (Dashboard, Notification, AddEditTransaction)
- **Use Cases** (SaveBudget, DeleteBudget, GetTransactions, dsb.)
- **Mapper** (TransactionEntity ↔ Transaction domain model)

```bash
./gradlew testDebugUnitTest
```

---

## 📌 Highlight Implementasi

- **Anti Double-Click** — `ClickExtension.kt` menyediakan `rememberSingleClick(600ms)` yang diterapkan ke seluruh tombol aksi (FAB, dialog konfirmasi, tombol simpan) untuk mencegah klik ganda yang tidak disengaja.
- **Custom Snackbar** — `AppSnackbarHost` + `AppSnackbar` dengan desain seragam Material 3 (rounded 12dp, warna aksi cyan) di seluruh 13 layar.
- **Recurring Budget** — Mekanisme *lazy generation* (tanpa WorkManager): periode anggaran berikutnya dibuat otomatis saat aplikasi dibuka, dengan safety cap 24 periode per-run.
- **IME-Aware Form** — Form tambah transaksi menggunakan `focusManager.clearFocus()` + `keyboard?.hide()` untuk navigasi field yang mulus dengan keyboard.

---

*Dikembangkan sebagai showcase Android development modern dengan Kotlin & Jetpack Compose.*
