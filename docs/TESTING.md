# Testing Strategy - NexCashApplication

## 1. Unit Testing
**Location**: `app/src/test/java/`
We focus on testing business logic, mappers, and ViewModels.
*   **Tools**: JUnit Jupiter (JUnit 5), `useJUnitPlatform()`, `kotlinx-coroutines-test`
    (untuk ViewModel: `Dispatchers.setMain` + virtual time), fake/repository in-memory
    untuk use case (tanpa mock berat).
*   **Goal**: Ensure data transformations (Mappers) and State transitions (ViewModels) work correctly.
*   **Contoh**: 74 unit test — 23 fitur anggaran berulang: mapper self-ref, lazy generation
    (`EnsureUpToDateBudgetsUseCaseTest`), alur simpan (`SaveBudgetUseCaseTest`), tutup seri
    (`DeleteBudgetUseCaseTest`) + `FakeBudgetRepository`; 17 fitur kategori tab utama:
    `CategoryViewModelTest` (load budget+terpakai, event→state/effect, search dengan
    debounce virtual time), `CategoryStateTest`, `CategoryItemTest`; **23 fitur detail
    kategori** (`feature/category/presentation/detail`): `DetailCategoryViewModelTest`
    (15 — load kategori+subkategori+transaksi terakhir+tren, budget pada window CUSTOM,
    regresi re-emit, jalur Error, 10 event→effect termasuk jembatan "Lihat Semua") dan
    `DetailCategoryModelTest` (8 — default state/model, mapper `Category.toUiModel`,
    properti `usedPercentage`/`remainingBudget`); 7 fitur halaman Semua
    Transaksi: `GetTransactionsByFilterUseCaseTest` (filter None/ByAccount, subtree
    subkategori reaktif, judul per filter) + `TransactionFilterTest` (4 — pemetaan
    argumen route primitif `All(categoryId, accountId)` ke `TransactionFilter` via
    `fromRoute`); dan 1 mapper (`BudgetMapperTest`) + `ExampleUnitTest` — fake in-memory
    per paket
    (`FakeBudgetRepository`, `FakeCategoryRepository`/`FakeTransactionRepository`,
    `FakeAccountRepository`). Aturan bisnisnya di
    [BUDGET_RECURRING.md](BUDGET_RECURRING.md); keputusan arsitekturnya di
    [ADR-002](adr/002-single-mvi-all-transactions.md).
*   **Catatan `toRoute()`**: `SavedStateHandle.toRoute()` tidak dapat diuji di JVM —
    `RouteDecoder` Navigation membaca argumen lewat Bundle Android yang di-mock
    (`getString` selalu null), sehingga nilai argumen diam-diam menjadi null tanpa
    error. VM yang membaca route (mis. `DetailCategoryViewModel`) kini menyediakan
    konstruktor `internal` penerima object route langsung untuk unit test, sementara
    konstruktor `@Inject` produksi tetap memakai `toRoute()` di device.

## 2. UI Testing (Instrumented)
**Location**: `app/src/androidTest/java/`
We test individual Composables and navigation flows.
*   **Tools**: Compose UI Test (ComposeTestRule), HiltAndroidTest.
*   **Goal**: Verify UI elements display correctly and respond to user interaction.

## 3. Running Tests
*   **All Unit Tests**: `./gradlew test`
*   **All UI Tests**: `./gradlew connectedAndroidTest`
*   **Specific Module**: `./gradlew :app:testDebugUnitTest`
