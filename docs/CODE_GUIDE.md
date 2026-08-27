# Code Guide & Walkthrough - NexCashApplication

## 1. Directory Structure Details
```text
app/src/main/java/com/example/nexcashapplication/
├── core/
│   ├── datasource/local/     # Room Entities, DAOs, Database
│   ├── di/                   # Hilt Modules
│   └── common/extentions/    # Kotlin extensions (e.g., ColorExt.kt)
├── feature/
│   ├── dashboard/            # BalanceCard, DashboardViewModel, DashboardScreen
│   ├── category/             # AddCategoryScreen, CategoryUiModel, Mappers
│   ├── account/              # AddEditInfoAccountScreen
│   └── transaction/          # Transaction history and input logic
└── ui/theme/                 # Material 3 Theme, Colors, Typography
```

## 2. Key Component Walkthrough

### 2.1. Transaction Entity (`core/datasource/local`)
The `TransactionEntity` is the core data model. It uses Foreign Keys to link with `AccountEntity` and `CategoryEntity`.

### 2.2. Balance Card (`feature/dashboard`)
A custom Composable that uses `bg_balance_card.jpg` and dynamic coloring via `ColorExt.kt` to display account balances.

### 2.3. Add Category Mapper (`feature/category/presentation/mapper`)
Uses `AddCategoryStateMapper` to convert UI state into domain/data entities, ensuring the ViewModels don't leak database logic to the UI.

## 3. UI State Management
We use `StateFlow` in ViewModels to emit immutable states to the Compose UI.
Example:
```kotlin
val uiState: StateFlow<DashboardUiState> = repository.getData()
    .map { data -> DashboardUiState.Success(data) }
    .stateIn(scope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)
```
