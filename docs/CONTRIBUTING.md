# Contribution Guidelines - NexCashApplication

## 1. Coding Standards
*   **Language**: Kotlin.
*   **Style**: Follow [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html).
*   **Naming**:
    *   Classes: `PascalCase`
    *   Functions/Variables: `camelCase`
    *   Composables: `PascalCase` (Noun-based, e.g., `BalanceCard`).

## 2. Git Flow & Branching
*   `main`: Stable release branch.
*   `develop`: Integration branch for features.
*   `feature/feature-name`: Branch for new features.
*   `bugfix/issue-name`: Branch for fixing bugs.

## 3. Pull Request (PR) Process
1.  Fork the repository and create your branch from `develop`.
2.  Ensure code compiles and all tests pass.
3.  Add tests for any new logic.
4.  Submit a PR with a clear description of changes.
5.  Use commit prefixes: `[Category]`, `[Dashboard]`, `[Core]`.
