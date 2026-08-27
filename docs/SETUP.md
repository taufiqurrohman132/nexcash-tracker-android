# Setup & Environment - NexCashApplication

## 1. Prerequisites
*   **Operating System**: Windows, macOS, or Linux.
*   **JDK**: Version 17 (Required).
*   **Android Studio**: Ladybug (2024.2.1) or newer recommended.
*   **Android SDK**: API 28 (Android 9.0) minimum.

## 2. Build Instructions
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/yourusername/NexCashApplication.git
    ```
2.  **Open in Android Studio**:
    *   File -> Open -> Select the project root.
3.  **Sync Gradle**:
    *   Click on "Sync Project with Gradle Files".
4.  **Run the App**:
    *   Select `app` configuration.
    *   Choose an Emulator or Physical Device.
    *   Click the **Run** (Green Play) button.

## 3. Environment Configuration
*   **Local Properties**: Ensure `local.properties` contains the correct `sdk.dir`.
*   **Gradle Properties**: `gradle.properties` includes KSP and Hilt configurations.

## 4. CI/CD (Future)
*   GitHub Actions configuration for automated builds and unit tests is planned.
