# Code Guide & Walkthrough - NexCashApplication

## 1. Directory Structure Details

```text
app/src/main/java/com/example/nexcashapplication/
├── MainActivity.kt           # Entry point, memuat NexcashApp
├── NexcashApp.kt             # Scaffold level app: bottom nav visibility + wiring NavHost
├── di/                       # Hilt modules (Database, Repository, UseCase, DataStore)
├── core/
│   ├── common/               # BaseViewModel, helper tanggal (DateUtils, DateFormatter, YearMonthExt)
│   ├── component/            # Composable reusable (AppButton, TransactionItem, MonthPicker, donutchart/)
│   ├── currency/             # Holder + format mata uang aplikasi (AppCurrencyHolder)
│   ├── datasource/local/     # Room Entities, DAOs, Database, DataStore, mapper, result
│   ├── domain/               # Model domain + interface repository
│   ├── extension/            # Extension Kotlin (formatCurrency, dll)
│   ├── i18n/                 # UiText + resolve string lintas locale
│   ├── navigations/          # Route (type-safe), NavigationStack (NavHost), BottomNavBar
│   ├── notification/         # Kanal notifikasi
│   └── ui/                   # Wrapper UiState
├── feature/                  # Satu paket per fitur (MVI/MVVM mandiri)
│   ├── account/  budgets/  category/  dashboard/  insight/
│   ├── notification/  settings/  statistics/  transaction/
│   └── common/               # components, extentions, transformation, utils lintas feature
└── ui/theme/                 # Material 3 theme, colors, typography
```

> **Target layout (belum diterapkan)** — rencana pemisahan layer jika `:app` dipecah
> jadi module:
>
> ```text
> core/
>   common/      <- util pure Kotlin (Result wrapper, dispatcher, dll) — TIDAK harus non-UI
>   ui/          <- shared Composable, UiState wrapper, UiText, theme helper
>   designsystem/<- component Compose reusable (Button, Card custom, dll)
>   domain/
>   data/
> feature/
>   category/    <- depend ke core:ui, core:common, core:domain
>   transaction/ <- depend ke core:ui, core:common, core:domain
> ```

## 2. Key Component Walkthrough

### 2.1. Transaction Entity (`core/datasource/local`)

The `TransactionEntity` is the core data model. It uses Foreign Keys to link with `AccountEntity`
and `CategoryEntity`.

### 2.2. Balance Card (`feature/dashboard`)

Renders the total balance on a linear gradient `PrimaryCyan` → `SecondaryDeepPurple`
(`Brush`/`Offset` — bukan gambar `bg_balance_card.jpg` lagi), dengan toggle mata untuk
menyembunyikan saldo dan baris amount yang anti-overflow (`maxLines = 1` +
`onTextLayout`). `BudgetCard` di paket yang sama memakai token warna tema
(`PrimaryCyan`, indicator `PrimaryCyan.copy(alpha = 0.1f)`) alih-alih hex hardcode.

### 2.3. Add Category Mapper (`feature/category/presentation/mapper`)

Uses `AddCategoryStateMapper` to convert UI state into domain/data entities, ensuring the ViewModels
don't leak database logic to the UI.

### 2.4. App Navigation (`core/navigations` + `NexcashApp`)

Type-safe route ada di `Route` (sealed interface `@Serializable`, mis.
`Route.TransactionRoute.Detail(transactionId)`) dan didaftarkan di `NavigationStack`
(satu `NavHost`, start destination `Route.HomeRoute.Main`).

Bottom bar berada di `Scaffold` level app (`NexcashApp`):

*   **Visibilitas** diturunkan dari `hierarchy` destination terhadap
    `TOP_LEVEL_DESTINATION` (Home, Transaksi, Aset, Statistik, Settings) — layar
    detail/tambah/hapus menyembunyikan bar dan memakai layar penuh.
*   Bar dibungkus `AnimatedVisibility` (slide masuk/keluar); `Scaffold` mengukur ulang
    bar tiap frame sehingga `innerPadding` mengikuti animasi dan konten tidak melompat.
*   Klik tab navigasi dengan `popUpTo(startDestination) { saveState = true }`,
    `restoreState = true`, `launchSingleTop = true` → tiap tab menyimpan back stack
    dan state ViewModel-nya sendiri.
*   `pendingDestination` (deep link notifikasi) dinavigasi lewat `LaunchedEffect`
    supaya layar tujuan terbuka **di atas** Dashboard dan tombol Kembali kembali ke
    Dashboard, bukan keluar aplikasi.

## 3. UI State Management

We use `StateFlow` in ViewModels to emit immutable states to the Compose UI.
Example:

```kotlin
val uiState: StateFlow<DashboardUiState> = repository.getData()
        .map { data -> DashboardUiState.Success(data) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)
```
