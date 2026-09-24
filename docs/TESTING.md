# Testing Strategy - NexCashApplication

## 1. Unit Testing
**Location**: `app/src/test/java/`
We focus on testing business logic, mappers, and ViewModels.
*   **Tools**: JUnit Jupiter (JUnit 5), `useJUnitPlatform()`. Fake/repository in-memory untuk use case (tanpa mock berat).
*   **Goal**: Ensure data transformations (Mappers) and State transitions (ViewModels) work correctly.
*   **Contoh**: 18 unit test fitur anggaran berulang — mapper self-ref, lazy generation
    (`EnsureUpToDateBudgetsUseCaseTest`), alur simpan (`SaveBudgetUseCaseTest`) +
    `FakeBudgetRepository`. Aturan bisnisnya di [BUDGET_RECURRING.md](BUDGET_RECURRING.md).

## 2. UI Testing (Instrumented)
**Location**: `app/src/androidTest/java/`
We test individual Composables and navigation flows.
*   **Tools**: Compose UI Test (ComposeTestRule), HiltAndroidTest.
*   **Goal**: Verify UI elements display correctly and respond to user interaction.

## 3. Running Tests
*   **All Unit Tests**: `./gradlew test`
*   **All UI Tests**: `./gradlew connectedAndroidTest`
*   **Specific Module**: `./gradlew :app:testDebugUnitTest`
