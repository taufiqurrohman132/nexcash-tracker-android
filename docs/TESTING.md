# Testing Strategy - NexCashApplication

## 1. Unit Testing
**Location**: `app/src/test/java/`
We focus on testing business logic, mappers, and ViewModels.
*   **Tools**: JUnit 4, Mockk, Truth.
*   **Goal**: Ensure data transformations (Mappers) and State transitions (ViewModels) work correctly.

## 2. UI Testing (Instrumented)
**Location**: `app/src/androidTest/java/`
We test individual Composables and navigation flows.
*   **Tools**: Compose UI Test (ComposeTestRule), HiltAndroidTest.
*   **Goal**: Verify UI elements display correctly and respond to user interaction.

## 3. Running Tests
*   **All Unit Tests**: `./gradlew test`
*   **All UI Tests**: `./gradlew connectedAndroidTest`
*   **Specific Module**: `./gradlew :app:testDebugUnitTest`
