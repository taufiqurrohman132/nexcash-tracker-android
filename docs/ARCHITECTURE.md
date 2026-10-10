# Architecture & Tech Stack - NexCashApplication

## 1. High-Level Architecture
NexCashApplication follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern. This ensures separation of concerns, testability, and maintainability.

### Layers:
*   **Presentation Layer**: Jetpack Compose UI, ViewModels, and UI States.
*   **Domain Layer (Optional/Implicit)**: Business logic, UseCases (if any), and Repository interfaces.
*   **Data Layer**: Repository implementations, Room Database (Local Datasource), and Preferences (DataStore).

## 2. Tech Stack
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/compose) - Declarative UI with Material 3.
*   **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Standard DI for Android.
*   **Local Database**: [Room](https://developer.android.com/training/data-storage/room) - SQLite abstraction for structured data.
*   **Asynchronous Programming**: [Kotlin Coroutines & Flow](https://developer.android.com/kotlin/coroutines) - For reactive data streams.
*   **Charts**: [Vico](https://github.com/patrykandpatrick/vico) - Modern charting library for Compose.
*   **Navigation**: [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) (`androidx.navigation:navigation-compose:2.9.8`) — type-safe routes via `kotlinx.serialization` (`@Serializable`).

## 3. Modularization Strategy
Currently, the project uses a **Feature-based structure** within a single `:app` module for simplicity, but it is structured to be easily split into library modules in the future:
*   `core/`: Shared components, utilities, and base data logic.
*   `feature/`: Independent UI features (Dashboard, Category, Account, Transaction).
