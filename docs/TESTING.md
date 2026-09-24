# Testing Strategy - NexCashApplication

## 1. Unit Testing
**Location**: `app/src/test/java/`
We focus on testing business logic, mappers, and ViewModels.
*   **Tools**: JUnit Jupiter (JUnit 5), `useJUnitPlatform()`, `kotlinx-coroutines-test`
    (untuk ViewModel: `Dispatchers.setMain` + virtual time), fake/repository in-memory
    untuk use case (tanpa mock berat).
*   **Goal**: Ensure data transformations (Mappers) and State transitions (ViewModels) work correctly.
*   **Contoh**: 47 unit test — 23 fitur anggaran berulang: mapper self-ref, lazy generation
    (`EnsureUpToDateBudgetsUseCaseTest`), alur simpan (`SaveBudgetUseCaseTest`), tutup seri
    (`DeleteBudgetUseCaseTest`) + `FakeBudgetRepository`; 17 fitur kategori:
    `CategoryViewModelTest` (load budget+terpakai, event→state/effect, search dengan
    debounce virtual time), `CategoryStateTest`, `CategoryItemTest` +
    `FakeCategoryRepository`/`FakeTransactionRepository`; dan 7 fitur halaman Semua
    Transaksi: `GetTransactionsByFilterUseCaseTest` (filter None/ByAccount, subtree
    subkategori reaktif, judul per filter) + `FakeTransactionRepository`/
    `FakeAccountRepository`. Aturan bisnisnya di
    [BUDGET_RECURRING.md](BUDGET_RECURRING.md); keputusan arsitekturnya di
    [ADR-002](adr/002-single-mvi-all-transactions.md).

## 2. UI Testing (Instrumented)
**Location**: `app/src/androidTest/java/`
We test individual Composables and navigation flows.
*   **Tools**: Compose UI Test (ComposeTestRule), HiltAndroidTest.
*   **Goal**: Verify UI elements display correctly and respond to user interaction.

## 3. Running Tests
*   **All Unit Tests**: `./gradlew test`
*   **All UI Tests**: `./gradlew connectedAndroidTest`
*   **Specific Module**: `./gradlew :app:testDebugUnitTest`
