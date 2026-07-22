# NexCash Application

NexCash is a modern personal finance management application built with **Jetpack Compose**. It helps users track their expenses, manage budgets, and organize financial transactions by categories.

## 🚀 Features

- **Interactive Dashboard**: Get a quick summary of your monthly spending and budget status using visual charts.
- **Transaction Management**: Easily record new transactions and view a detailed history of your spending.
- **Category & Budgeting**: Create and manage categories with specific budget limits. Track how much you've spent versus your remaining balance.
- **Sub-category Support**: Further organize your expenses with nested sub-categories.
- **Modern UI/UX**: Built entirely with Material 3 components for a sleek and responsive experience.

## 🛠️ Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/compose)
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Navigation**: [Navigation 3](https://developer.android.com/jetpack/androidx/releases/navigation3) (Experimental)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Charts**: [Vico](https://github.com/patrykandpatrick/vico) for financial data visualization.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/compose/)
- **Serialization**: Kotlinx Serialization & Gson

## 📂 Project Structure

```text
com.example.nexcashapplication
├── core                # Common components, navigation logic, and extensions
│   ├── component       # Reusable UI components (TransactionItem, etc.)
│   ├── extension       # Kotlin extension functions
│   └── navigations     # NavigationRoot and route definitions
├── feature             # Module-based features
│   ├── dashboard       # Dashboard UI and domain models
│   ├── transaction     # Transaction screens and logic
│   └── category        # Category and sub-category management
└── ui.theme            # App theme, typography, and color palette
```

## 🛠️ Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 28+ (Min SDK)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/NexCashApplication.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or a physical device (API 28+).

## 🧪 Testing
The project includes unit tests and instrumented tests to ensure reliability.
- Run unit tests: `./gradlew test`
- Run instrumented tests: `./gradlew connectedAndroidTest`

---
*Developed as a modern Android development showcase.*
