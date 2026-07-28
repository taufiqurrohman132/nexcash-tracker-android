# NexCash Application

NexCash is a modern personal finance management application built with **Jetpack Compose**. It helps users track their expenses, manage budgets, and organize financial transactions by categories.

## 🚀 Features

- **Interactive Dashboard**: Get a quick summary of your monthly spending and budget status using visual charts.
- **Transaction Management**: Easily record new transactions and view a detailed history of your spending.
- **Asset & Investment Tracking**: Monitor various asset types including Stocks, Crypto, Gold, and Mutual Funds.
- **Category & Budgeting**: Create and manage categories with specific budget limits and track spending.
- **Bill Management**: Keep track of your recurring bills and upcoming payments.
- **Financial Insights**: Gain deeper understanding of your financial habits with detailed analytics.
- **Notification System**: Receive timely alerts for budgets, reminders, and transaction updates.
- **Modern UI/UX**: Built entirely with Material 3 components for a sleek and responsive experience.

## 🛠️ Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/compose)
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Navigation**: [Navigation 3](https://developer.android.com/jetpack/androidx/releases/navigation3) (Experimental)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room) with Paging 3 support.
- **Data Persistence**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for user preferences.
- **Charts**: [Vico](https://github.com/patrykandpatrick/vico) for financial data visualization.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/compose/)
- **Serialization**: Kotlinx Serialization & Gson

## 📂 Project Structure

```text
com.example.nexcashapplication
├── core                # Common components, navigation logic, and extensions
│   ├── component       # Reusable UI components (TransactionItem, etc.)
│   ├── datasource      # Local database (Room) and Preferences (DataStore)
│   ├── extension       # Kotlin extension functions
│   └── navigations     # NavigationRoot, Navigator, and Route definitions
├── feature             # Module-based features
│   ├── dashboard       # Dashboard UI and main overview
│   ├── transaction     # Transaction recording and history
│   ├── category        # Category and budget management
│   ├── aset            # Asset and investment tracking
│   ├── insight         # Financial analytics and reports
│   ├── tagihan         # Bill and payment management
│   └── settings        # User preferences and app settings
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
