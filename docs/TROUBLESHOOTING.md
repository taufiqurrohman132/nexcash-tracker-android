# Troubleshooting - NexCashApplication

## 1. Build Issues
### Hilt/KSP Errors
*   **Issue**: `Hilt_DashboardViewModel_Factory.java` not found or compilation errors in generated code.
*   **Solution**: Run `Build -> Clean Project` then `Build -> Rebuild Project`. Ensure the KSP version matches your Kotlin version.

### Missing Resources
*   **Issue**: `bg_balance_card` or other drawables not recognized.
*   **Solution**: Check `app/src/main/res/drawable/` and ensure naming is strictly lowercase with underscores.

## 2. Runtime Issues
### Database Migrations
*   **Issue**: App crashes after modifying `Entity` classes.
*   **Solution**: During development, you can use `.fallbackToDestructiveMigration()` in `DatabaseModule.kt`. For production, proper Room migrations must be written.

### UI Rendering
*   **Issue**: Compose Preview not loading.
*   **Solution**: Ensure the Preview has a `NexCashTheme` wrapper and all Hilt-injected ViewModels are provided via fake data or `CompositionLocal`.
