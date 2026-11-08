# Testing Strategy - NexCashApplication

## 1. Unit Testing
**Location**: `app/src/test/java/`
We focus on testing business logic, mappers, and ViewModels.
*   **Tools**: JUnit Jupiter (JUnit 5), `useJUnitPlatform()`, `kotlinx-coroutines-test`
    (untuk ViewModel: `Dispatchers.setMain` + virtual time via `runTest`), fake/repository in-memory
    untuk use case (tanpa mock berat).
*   **Goal**: Ensure data transformations (Mappers) and State transitions (ViewModels) work correctly.
*   **Cakupan Unit Test (Total 375 Test)**:
    *   **Fitur Pengaturan (`feature/settings`)**: 44 test mencakup domain, presentation, dan state management:
        *   **Domain Models**:
            *   `AppSettingsTest` (2 test) — pengujian nilai default domain model `AppSettings` dan verifikasi integritas mutasi `.copy()`.
            *   `AppThemeModeTest` (3 test) — pengujian pemetaan enum `AppThemeMode` dari/ke string DataStore serta fallback aman ke `SYSTEM`.
        *   **UI Models & State**:
            *   `LanguageItemUiModelTest` (3 test) — pemetaan ekstensi `AppLanguage.nameResId` ke resource string ID dan integritas model `LanguageItemUiModel`.
            *   `LanguageSettingsStateTest` (4 test) — nilai default `LanguageSettingsState` dan logika komputasi flag `hasChange`.
            *   `SettingsStateTest` (2 test) — nilai default `SettingsState` dan verifikasi mutasi state `.copy()`.
        *   **ViewModels (`presentation`)**:
            *   `LanguageSettingsViewModelTest` (11 test) — inisialisasi preferensi bahasa, stabilitas saat DataStore error, pemilihan bahasa (`OnItemClick`), navigasi kembali (`OnBackClick`), simpan bahasa dengan efek `Saved` & `AppLocale.apply()`, penanganan error simpan (`ShowSnackbar`), pencarian bahasa (`OnSearchQueryChange` ber-debounce 300ms), serta pembersihan pencarian (`OnSearchClear`).
            *   `SettingsViewModelTest` (10 test) — inisialisasi mata uang/bahasa/tema/warna hex ke Compose `Color`, fallback `PrimaryCyan`, pembukaan sheet tema `TEMA_WARNA` tanpa navigasi, navigasi menu lainnya (`NavigateTo`), penutupan sheet (`OnThemeSheetDismiss`), pemilihan tema & warna dengan penanganan error ke `ShowSnackbar`.
            *   `CurrencySettingsViewModelTest` (9 test) — pemuatan daftar mata uang, inisialisasi pilihan tersimpan, fallback mata uang tidak dikenal, pemilihan item (`OnItemClick`), simpan & navigasi kembali, serta pencarian mata uang ber-debounce.
        *   **Fakes & Stubs**: `FakeSettingsRepository` — repositori in-memory reaktif dengan dukungan nilai awal kustom, simulasi exception, dan pelacakan perubahan (`savedThemeMode`, `savedColor`, `savedLanguage`, `savedCurrency`).
    *   **Fitur Kategori (`feature/category`)**: 104 test mencakup seluruh layer fitur:
        *   **Domain Use Cases & Models**:
            *   `DeleteCategoryUseCaseTest` (3 test) — pengujian aksi hapus dengan resolusi `DeleteAllTransactions`, `MoveToCategory(id)`, dan `Uncategorize`.
            *   `GetCategoryByIdUseCaseTest` (2 test) — pengujian query kategori berdasarkan ID dan fallback null.
            *   `GetSubCategoriesWithBudgetUseCaseTest` (4 test) — logika hybrid anggaran induk & subkategori (rollup keluarga saat induk ber-budget, perhitungan window sendiri pada sub ber-budget, isolasi transaksi di luar window, serta fallback 0L saat induk belum ber-budget).
            *   `CategoryDeleteResolutionTest` (3 test) — verifikasi domain model `CategoryDeleteResolution`, `CategoryName`, dan `WeeklyTrend`.
        *   **ViewModels (`presentation`)**:
            *   `AddCategoryViewModelTest` (13 test) — mode tambah baru (recent colors & default recent icons via `PreferencesRepository`), mode edit kategori eksisting, mode subkategori (pemuatan info induk), penanganan event form (`OnNameChanged`, `OnNotesChanged`, `OnTypeSelected`, `OnChangeIcon`, `OnColorSelected`, dialog color picker), dan pengiriman `NavigateBack` serta `insertCategory`/`updateCategory` saat valid/invalid.
            *   `DeleteCategoryViewModelTest` (11 test) — inisialisasi load detail, jumlah transaksi, filter kategori tujuan (mengecualikan ID kategori yang akan dihapus), event dropdown dan pemilihan opsi, serta eksekusi `ConfirmDelete` dengan guard `isConfirmEnabled`.
            *   `SubCategoryListViewModelTest` (4 test) — pemuatan subkategori ber-budget, event navigasi kembali & detail subkategori, serta mekanisme retry saat gagal.
            *   `CategoryViewModelTest` (9 test) — load expense & income list dengan budget aktif dan terpakai, rollup subkategori, navigasi tab/page, dan search ber-debounce 300ms dengan virtual time.
            *   `DetailCategoryViewModelTest` (15 test) — load kategori, subkategori, transaksi terakhir, tren mingguan, budget window kustom, regresi re-emit Room, dan 10 event navigasi.
        *   **Mappers & UI States**:
            *   `AddCategoryStateMapperTest` (3 test) — konversi `AddCategoryState.toDomain()` dan `Category.toUiState()`.
            *   `CategoryStateMapperTest` (3 test) — konversi `toCategoryItem()`, `toCategoryItemList()`, dan `toCategoryNameItemUiModel()`.
            *   `DeleteCategoryStateMapperTest` (2 test) — konversi `CategoryName.toDeleteUiModel()` dan `toCategoryDeleteItemList()`.
            *   `DetailCategoryStateMapperTest` (2 test) — konversi `Category.toUiModel()` dengan default budget.
            *   `AddCategoryStateTest` (2 test) — validitas form (`isValid` berdasarkan nama).
            *   `DeleteCategoryStateTest` (3 test) — properti `isConfirmEnabled` untuk setiap opsi delete.
            *   `BudgetPeriodTest` (1 test) — verifikasi label enum periode anggaran.
            *   `CategoryStateTest` & `CategoryItemTest` (8 test) — kalkulasi total anggaran, sisa anggaran, dan persentase terpakai.
            *   `DetailCategoryModelTest` (8 test) — persentase dan sisa budget kartu detail.
        *   **Fakes & Stubs**: `FakeCategoryRepository` (reaktif dengan tracking insert/update/delete & exception simulation) dan `FakePreferencesRepository` (in-memory DataStore preferences).
    *   **Fitur Anggaran (`feature/budgets`)**: 23+ test — series + instance recurring budget, lazy generation (`EnsureUpToDateBudgetsUseCaseTest`), simpan & migrasi seri (`SaveBudgetUseCaseTest`), tutup seri (`DeleteBudgetUseCaseTest`), serta periode anggaran (`GetBudgetPeriodUseCaseTest`).
    *   **Fitur Akun (`feature/account`)**: Use case delete/summary/validation, view models, dan mappers.
    *   **Fitur Transaksi (`feature/transaction`)**: `GetTransactionsByFilterUseCaseTest` (filter None/ByAccount/ByCategory, subtree BFS), pemetaan filter route `TransactionFilterTest`, mapper transaksi, dan ViewModel transaksi.
*   **Konvensi Arsitektur `toRoute()`**: `SavedStateHandle.toRoute()` tidak dapat diuji di JVM murni karena `RouteDecoder` Navigation membaca argumen lewat Bundle Android yang di-mock (`getString` selalu null tanpa error). ViewModel yang membaca typed route (`AddCategoryViewModel`, `DeleteCategoryViewModel`, `DetailCategoryViewModel`, `SubCategoryListViewModel`, `DetailAccountViewModel`, `AddEditBudgetViewModel`, dll.) menyediakan konstruktor `internal` penerima objek `Route` langsung untuk pengujian unit, sementara konstruktor `@Inject constructor(savedStateHandle: SavedStateHandle, ...)` tetap dipakai produksi di device via Hilt.

## 2. UI Testing (Instrumented)
**Location**: `app/src/androidTest/java/`
We test individual Composables and navigation flows.
*   **Tools**: Compose UI Test (ComposeTestRule), HiltAndroidTest.
*   **Goal**: Verify UI elements display correctly and respond to user interaction.

## 3. Running Tests
*   **All Unit Tests**: `./gradlew test` (atau `./gradlew :app:testDebugUnitTest`)
*   **Specific Package / Class**: `./gradlew testDebugUnitTest --tests "com.example.nexcashapplication.feature.category.*"`
*   **All UI Tests**: `./gradlew connectedAndroidTest`
